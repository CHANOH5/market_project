package com.market.market.auth.dto;

public record LoginRequest(
        String email,
        String password
) {
}