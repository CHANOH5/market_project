package com.market.market.auth.dto;

public record SignupRequest(
        String username,
        String email,
        String password,
        String phone
) {
}