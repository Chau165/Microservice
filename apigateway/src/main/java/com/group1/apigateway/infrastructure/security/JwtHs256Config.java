package com.group1.apigateway.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtHs256Config {

    /**
     * Tạo ReactiveJwtDecoder xác thực JWT ký bằng HS256 (shared-secret).
     * Secret phải khớp với secret của AUTH-SERVICE.
     * Ưu tiên env var JWT_SECRET; fallback về giá trị mặc định trong yml.
     */
    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(
            @Value("${security.jwt.secret}") String secret) {

        byte[] keyBytes = Base64.getDecoder().decode(secret);
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");

        return NimbusReactiveJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
