package com.group1.franchiseservice.model.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
