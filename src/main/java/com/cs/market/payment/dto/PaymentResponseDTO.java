package com.cs.market.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentResponseDTO {

    private String status;              // "succeeded" / "failed"
    private String transactionId;
    private String message;              // 실패 사유

} // end class
