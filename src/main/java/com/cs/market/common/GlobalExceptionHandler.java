package com.cs.market.common;

import com.cs.market.global.exception.PaymentProviderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SimpleError> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new SimpleError("BAD_REQUEST", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<SimpleError> handleIllegalState(IllegalStateException e) {
        String msg = e.getMessage() == null ? "Illegal state" : e.getMessage();
        if (msg.startsWith("HTTP_ERROR")) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new SimpleError("PAYMENT_GATEWAY_ERROR", msg));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new SimpleError("INVALID_STATE", msg));
    }

    @ExceptionHandler(PaymentProviderException.class)
    public ResponseEntity<Map<String, Object>> handlePayment(PaymentProviderException e) {
        int status = e.getHttpStatus() > 0 ? e.getHttpStatus() : 502;
        return ResponseEntity.status(status).body(
                Map.of("status", "FAILED", "message", e.getMessage())
        );
    }

}
