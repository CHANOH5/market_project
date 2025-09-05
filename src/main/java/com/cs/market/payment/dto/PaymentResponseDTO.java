package com.cs.market.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentResponseDTO {

    private String paymentId;
    private String status;              // "succeeded" / "failed"
    private String approveAt;
    private String reason;              // 실패 사유

} // end class
