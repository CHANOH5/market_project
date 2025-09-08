package com.cs.market.payment.controller;

import com.cs.market.payment.dto.PaymentRequestDTO;
import com.cs.market.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{orderId}")
    public Mono<ResponseEntity<Void>> pay(@PathVariable Long orderId,
                                          @RequestBody PaymentRequestDTO dto) {
        return paymentService.pay(orderId, dto)
                .thenReturn(ResponseEntity.ok().build());
    }

}
