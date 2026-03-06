package service.CSFC.CSFC_auth_service.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String privateKey,
        String publicKey,
        long accessTokenExpiration,
        long refreshTokenExpiration
) {}