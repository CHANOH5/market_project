package com.cs.market.payment.service;

import com.cs.market.order.entity.Order;
import com.cs.market.order.entity.OrderStatus;
import com.cs.market.order.repository.OrderRepository;
import com.cs.market.payment.client.PaymentClient;
import com.cs.market.payment.dto.PaymentRequestDTO;
import com.cs.market.payment.dto.PaymentResponseDTO;
import com.cs.market.payment.entity.*;
import com.cs.market.payment.repository.PaymentAttemptAuditRepository;
import com.cs.market.payment.repository.PaymentAttemptRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    /**
     * 입력(주문ID, 결제정보) → 외부호출 → 결과 반영.
     * 어떤 부수효과(DB 변경)가 언제 일어나야 올바른가?
     */

    /**
     * "결제 가능 상태의 주문"을 받아 결제 시도 기록 -> 외부 PG 호출
     *  -> 성공/실패에 따른 상태 전이 및 재고 복원 -> 결제 이력/감사 로그 저장을 트랜잭션 경계를 지키며 관리
     *  DB 쓰기(JPA)는 블로킹 -> Schedulers.boundedElastic()에서 실행
     *  외부 호출은 WebClient(논블로킹)
     *  트랜잭션은 짧게 : 준비(TX1) <-> 반영(TX2)
     *  멱등성 보장: orderId, idempotencyKey로 중복 결제 차단
     *  PAYMENT_PENDING -> PAID | PAYMENT_FAILED
     */


    private final OrderRepository orderRepository;
    private final PaymentAttemptRepository attemptRepository;
    private final PaymentAttemptAuditRepository attemptAuditRepository;
    private final PaymentClient paymentClient;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public PaymentService(OrderRepository orderRepository, PaymentAttemptRepository attemptRepository, PaymentAttemptAuditRepository attemptAuditRepository, PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.attemptRepository = attemptRepository;
        this.attemptAuditRepository = attemptAuditRepository;
        this.paymentClient = paymentClient;
    }

    /**
     * 최상위 유스케이스, 전체 결제 플로우를 리액티브 파이프라인으로 구성
     * prepareAtytemptBlocking() 으로 결제 시도 생성(TX1)
     * paymentClient.charge()로 PG 호출(논블로킹) -> 외부 I/O는 논블로킹으로 동시성 처리량 증가
     * 응답에 따라 결과 반영(TX2) -> DB는 짧은 블로킹 TX로 락 점유률 감소
     * 모든 단계에서 감사 로그 남김
     * @param orderId   결제 대상을 식별
     * @param dto       결제수단/카드/통화/멱등키 정보가 들어있는 결제 입력값 캡술화
     * @return          Mono<Void> 비동기 완료 신호로 완료되면 200 응답 같은 응답 반환
     */
    public Mono<Void> pay(Long orderId, PaymentRequestDTO dto) {
        return Mono.fromCallable(() -> prepareAttemptBlocking(orderId, dto))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(attempt -> {
                    // 감사 로그: REQUEST
                    return Mono.fromRunnable(() ->
                                    saveAudit(attempt.getId(),
                                            AuditEvent.REQUEST,
                                            null,
                                            null,
                                            toJson(dto),
                                            null))
                            .subscribeOn(Schedulers.boundedElastic())
                            .then(
                                    paymentClient.charge(orderId, attempt.getAmount().longValue(), attempt.getIdempotencyKey())
                                            .flatMap(resp -> handleSuccess(attempt, orderId, resp))
                                            .onErrorResume(ex -> handleFailure(attempt, orderId, ex))
                            );
                })
                .then();
    } // end class

    // =========== 블로킹 섹션 ============

    /**
     * 외부 호출 전에 이 주문이 결제 가능한 상태인지, 중복 요청인지를 ***DB에서 확정해야 함***
     * TX1(짧게) : 결제 가능성 점검 & 멱등성 보장 * 시도 레코드 생성
     * 주문 조회 및 상태 검증(PAYMENT_PENDING 인지)
     * 총액 계산 (주문 시점의 스냅샷 기반)
     * idempotencyKey 결정(요청에 없으면 서버가 생성)
     * 이미 같은 (orderId, idempotencyKey)가 있으면 재사용 -> 중복 결제 차단
     * PaymentAttempt.requested() 저장
     * 감사 로그 찍기
     * @param orderId   결제 대상 식별
     * @param dto       제수단/카드/통화/멱등키 정보가 들어있는 결제 입력값 캡술화
     * @return
     */
    @Transactional
    public PaymentAttempt prepareAttemptBlocking(Long orderId, PaymentRequestDTO dto) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));

        if(order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("결제가 가능한 상태가 아닙니다.");
        } // if

        BigDecimal amount = order.getTotalAmount();
        String idempotencyKey = (dto.getIdempotencyKey() != null && !dto.getIdempotencyKey().isBlank())
                ? dto.getIdempotencyKey()
                : UUID.randomUUID().toString();

        Optional<PaymentAttempt> dup = attemptRepository.findByOrderIdAndIdempotencyKey(orderId, idempotencyKey);
        if(dup.isPresent()) {
            return dup.get();
        }

        PaymentAttempt attempt = PaymentAttempt.of(orderId, "MOCK", idempotencyKey, amount);
        attemptRepository.save(attempt);

        // 감사 로그
//        saveAudit(attempt.getId(), "REQUEST", null,  null, toJson(dto));

        return attempt;
    } // prepareAttemptBlocking

    /**
     * PG가 승인한 시점에만 "주문 확정"이 가능
     * 전이는 엔티티 메서드로 강제하여 상태머신 규칙을 엔티티 내부에서 보장
     * PaymentAttempt 상태를 SUCCESS로 전이(+ 외부 거래 ID 저장)
     * Order를 PAID로 전이(선재고 차감 확정)
     * @param orderId               결제 대상 식별
     * @param atteptId
     * @param externalPaymentId     PG 거래ID: CS/환불 추적용
     */
    @Transactional
    public void markSuccessBlocking(Long orderId, Long atteptId, String externalPaymentId) {
        PaymentAttempt attempt = attemptRepository.findById(atteptId).orElseThrow();
        if (attempt.getStatus() != PaymentStatus.PENDING) {
            return; // 멱등성 보장
        }
        attempt.success(externalPaymentId);
        attemptRepository.save(attempt);

        Order order = orderRepository.findById(orderId).orElseThrow();
        order.markPaid();
        orderRepository.save(order);
    } // markSuccessBlocking

    /**
     * 선재고 차감 때문에 실패 시 반드시 재고 원복이 필요
     * TX2(실패 반영)
     * 1. PaymentAttempt 상태를 FAILURE로 전이
     * 2. Order를 PAYMENT_FAILED로 전이
     * 3. 재고 복원
     * @param orderId
     * @param attemptId
     */
    @Transactional
    public void markFailedBlocking(Long orderId, Long attemptId, String failureCode, String externalPaymentId) {
        PaymentAttempt attempt = attemptRepository.findById(orderId).orElseThrow();
        if (attempt.getStatus() != PaymentStatus.PENDING) {
            return; // 멱등성 보장
        }
        attempt.fail(failureCode, externalPaymentId);
        attemptRepository.save(attempt);

        Order order = orderRepository.findById(orderId).orElseThrow();
        order.markPaymentFailed();
        orderRepository.save(order);
    } // markFailedBlocking

    /**
     * 운영/CS/장애 분석을 위해 입출력 원문과 상태, 시간을 남기면 추적 가능성이 증가함
     * 감사 테이블에 이벤트 히스토리를 남김(REQUEST/SUCCESS/FAILURE)
     * @param attemptId     어떤 시도에 대한 로그인지
     * @param event         REQUEST|SUCCESS|FAILURE
     * @param httpStatus    외부 응답 코드
     * @param errMsg        에러 메시지
     * @param respJson      응답 원문
     */
    private void saveAudit(Long attemptId,
                           AuditEvent event,
                           Integer httpStatus,
                           String errMsg,
                           String reqJson,
                           String respJson) {
        PaymentAttemptAudit audit = PaymentAttemptAudit.of(
                attemptId,
                AuditLevel.INFO,
                event,
                httpStatus,
                reqJson,
                respJson,
                null,
                errMsg
        );
        attemptAuditRepository.save(audit);
    }

    // ====== 논블로킹 보조 ======

    private Mono<Void> handleSuccess(PaymentAttempt attempt, Long orderId, PaymentResponseDTO resp) {
        return Mono.fromRunnable(() -> {
                    markSuccessBlocking(orderId, attempt.getId(), resp.getTransactionId());
                    saveAudit(attempt.getId(), AuditEvent.SUCCESS, 200, null, null, toJson(resp));
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private Mono<Void> handleFailure(PaymentAttempt attempt, Long orderId, Throwable ex) {
        return Mono.fromRunnable(() -> {
                    saveAudit(attempt.getId(), AuditEvent.FAILURE, null, ex.getMessage(), null, null);
                    String failureCode = "PROVIDER_ERROR";
                    String externalId = null;
                    markFailedBlocking(orderId, attempt.getId(), failureCode, externalId);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    /**
     * 요청/응답/오류를 JSON 문자열로 직렬화해서 감사 로그에 저장
     * @param o
     * @return
     */
    private String toJson(Object o) {
        try {
            return o == null ? null : objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return "{\"jsonError\":\"" + e.getMessage() + "\"}";
        }
    }

} // end class

