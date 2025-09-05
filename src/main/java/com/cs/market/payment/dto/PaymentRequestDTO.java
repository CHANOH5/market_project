package com.cs.market.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequestDTO {

    private String method;              // "CARD"
    private String card;                // "4242" (모의)
    private String currency;            // "KRW"
    private String idempotencyKey;      // 클라이언트 생성 or 서버 생성

} // end class
