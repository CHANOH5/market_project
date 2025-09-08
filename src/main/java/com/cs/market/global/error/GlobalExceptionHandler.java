package com.cs.market.global.error;

import com.cs.market.global.exception.PaymentProviderException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentProviderException.class)
    public ResponseEntity<Map<String, Object>> handlePayment(PaymentProviderException e) {
        int status = e.getHttpStatus() > 0 ? e.getHttpStatus() : 502;
        return ResponseEntity.status(status).body(
                Map.of("status", "FAILED", "message", e.getMessage())
        );
    }

}
