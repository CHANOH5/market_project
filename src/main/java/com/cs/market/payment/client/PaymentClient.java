package com.cs.market.payment.client;

import com.cs.market.global.exception.PaymentProviderException;
import com.cs.market.payment.dto.PaymentResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class PaymentClient {

   private final WebClient webClient;

    public PaymentClient(@Qualifier("paymentWebClient") WebClient webClient) {
        this.webClient = webClient;
    } // constructor

    public Mono<PaymentResponseDTO> charge(Long orderId, long amount, String idemKey) {
        return webClient.post()
                .uri("/api/v1/payment")
                .header("Idempotency-Key", idemKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(Map.of(
                        "orderId", String.valueOf(orderId),
                        "amount", amount
                ))
                .retrieve()
                .onStatus(s -> s.is4xxClientError(),
                        r -> r.bodyToMono(String.class)
                                .flatMap(b -> Mono.error(new PaymentProviderException("4xx error: " + b))))
                .onStatus(s -> s.is5xxServerError(),
                        r -> r.bodyToMono(String.class)
                                .flatMap(b -> Mono.error(new PaymentProviderException("5xx error: " + b))))
                .bodyToMono(PaymentResponseDTO.class);
    }

} // end class
