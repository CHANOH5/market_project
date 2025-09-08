package com.cs.market.payment.repository;

import com.cs.market.payment.entity.PaymentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, Long> {

    Optional<PaymentAttempt> findByOrderIdAndIdempotencyKey(Long orderId, String idempotencyKey);

} // end class
