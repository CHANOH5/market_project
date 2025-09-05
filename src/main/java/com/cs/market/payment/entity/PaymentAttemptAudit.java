package com.cs.market.payment.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

public class PaymentAttemptAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="attempt_id", nullable = false)
    private Long attemptId;

    @Enumerated(EnumType.STRING)
    @Column(name="level", nullable = false, length = 10)
    private AuditLevel level; // INFO/WARN/ERROR

    @Enumerated(EnumType.STRING)
    @Column(name="event", nullable = false, length = 20)
    private AuditEvent event; // REQUEST/CALLBACK/SUCCESS/FAILURE

    @Column(name="error_code", length = 100)
    private String errorCode;

    @Column(name="error_message", length = 500)
    private String errorMessage;

    @Column(name="http_status")
    private Integer httpStatus;

    @Lob
    @Column(name="request_payload")
    private String requestPayload; // JSON 원문

    @Lob
    @Column(name="response_payload")
    private String responsePayload; // JSON 원문

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    public static PaymentAttemptAudit of(Long attemptId,
                                         AuditLevel level,
                                         AuditEvent event,
                                         Integer httpStatus,
                                         String reqJson,
                                         String respJson,
                                         String errCode,
                                         String errMsg) {
        PaymentAttemptAudit paa = new PaymentAttemptAudit();
        paa.attemptId = attemptId;
        paa.level = level;
        paa.event = event;
        paa.httpStatus = httpStatus;
        paa.requestPayload = reqJson;
        paa.responsePayload = respJson;
        paa.errorCode = errCode;
        paa.errorMessage = errMsg;

        return paa;
    }

} // end class
