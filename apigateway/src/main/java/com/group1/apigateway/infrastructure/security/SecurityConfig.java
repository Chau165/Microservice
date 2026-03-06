package com.group1.apigateway.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class SecurityConfig {

    @Value("${security.jwt.public-key}")
    private String publicKeyString;

    @Bean
    public ReactiveJwtDecoder jwtDecoder() throws Exception {

        String key = publicKeyString
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(spec);

        return NimbusReactiveJwtDecoder.withPublicKey(rsaPublicKey).build();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ServerAuthenticationEntryPoint authenticationEntryPoint,
            ServerAccessDeniedHandler accessDeniedHandler,
            InternalHeaderInjectionWebFilter internalHeaderInjectionWebFilter,
            IpRateLimitWebFilter ipRateLimitWebFilter
    ) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeExchange(ex -> ex
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/api/auth-service/**").permitAll()
                        .pathMatchers("/api/internal/echo/**").authenticated()
                        .pathMatchers("/api/**").authenticated()
                        .anyExchange().permitAll()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .jwt(jwt -> {})
                )

                .addFilterAt(ipRateLimitWebFilter, SecurityWebFiltersOrder.FIRST)
                .addFilterAfter(internalHeaderInjectionWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .build();
    }
}