package com.cs.market.payment.repository;

import com.cs.market.payment.entity.PaymentAttemptAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentAttemptAuditRepository extends JpaRepository<PaymentAttemptAudit, Long> {

} // end class
