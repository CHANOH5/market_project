package com.cs.market.order.controller;

import com.cs.market.cart.dto.CartLine;
import com.cs.market.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestParam Long userId,
            @RequestBody List<CartLine> items
    ) {
        Long orderId = orderService.createOrder(userId, items);
        return ResponseEntity.ok(Map.of("orderId", orderId, "status", "PAYMENT_PENDING"));
    }
}
