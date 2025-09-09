package com.cs.market.payment.repository;

import com.cs.market.payment.entity.PaymentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, Long> {

    /** 동일 (orderId, idemKey)로 들어오면 새 시도 생성 없이 기존 결과 재사용. */
    Optional<PaymentAttempt> findByOrderIdAndIdempotencyKey(Long orderId, String idempotencyKey);

} // end class
