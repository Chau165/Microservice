package com.group1.franchiseservice.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String privateKey,
        String publicKey,
        long accessTokenExpMinutes,
        long refreshTokenExpDays
) {}