package com.cs.market.payment.client;

import com.cs.market.payment.dto.PaymentRequestDTO;
import com.cs.market.payment.dto.PaymentResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class PaymentClient {

   private final WebClient webClient;

    public PaymentClient(WebClient webClient) {
        this.webClient = webClient;
    } // constructor

    public Mono<PaymentResponseDTO> charge(PaymentRequestDTO dto, long amount, String idemKey) {

    }


} // end class
