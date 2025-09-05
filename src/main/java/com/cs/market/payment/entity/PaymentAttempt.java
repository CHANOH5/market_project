package com.cs.market.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "payment_attempt")
public class PaymentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="order_id", nullable = false)
    private Long orderId;

    @Column(name="provider", nullable = false, length = 50)
    private String provider; // "MOCK" | "BEECEPTOR" | ...

    @Column(name="idempotency_key", nullable = false, length = 128)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false, length = 16)
    private PaymentStatus status;

    @Column(name="external_payment_id", length = 255)
    private String externalPaymentId;

    @Column(name="amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 255)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static PaymentAttempt of(Long orderId, String provider, String idempotencyKey, BigDecimal amount) {

        if (orderId == null) {
            throw new IllegalArgumentException("orderId required");
        }
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey required");
        }
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("amount must be >= 0");
        }

        PaymentAttempt pa = new PaymentAttempt();
        pa.orderId = orderId;
        pa.provider = (provider != null) ? provider : "MOCK" ;
        pa.idempotencyKey = idempotencyKey;
        pa.status = PaymentStatus.PENDING;
        pa.amount = amount;
        pa.createdBy = "SYSTEM";

        return pa;
    }

    /** 상태 전이 - 결제 성공 */
    public void success(String externalPaymentId) {
        if(this.status != PaymentStatus.PENDING) {

        }
        if(externalPaymentId == null || externalPaymentId.isEmpty()) {

        }

        this.status = PaymentStatus.SUCCESS;
        this.externalPaymentId = externalPaymentId;

    }

    /** 상태 전이 - 결제 실패 */
    public void fail(String externalPaymentId) {
        if(this.status != PaymentStatus.PENDING) {

        }
        if(externalPaymentId == null || externalPaymentId.isEmpty()) {

        }

        this.status = PaymentStatus.FAILURE;
        this.externalPaymentId = externalPaymentId;
        // 실패를 Audit에 던져줘야하지않을까?
    }

} // end class
