package com.group1.apigateway.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;

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

    /**
     * Public chain: handles /api/auth-service/** WITHOUT JWT validation.
     * Prevents oauth2ResourceServer from rejecting requests with invalid/expired tokens
     * on public auth endpoints (e.g. login with a cached bad token in the client).
     */
    @Bean
    @Order(1)
    public SecurityWebFilterChain publicFilterChain(
            ServerHttpSecurity http,
            IpRateLimitWebFilter ipRateLimitWebFilter
    ) {
        return http
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/auth-service/**"))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex.anyExchange().permitAll())
                .addFilterAt(ipRateLimitWebFilter, SecurityWebFiltersOrder.FIRST)
                .build();
    }

    /**
     * Secured chain: handles all other paths, requires valid JWT for protected endpoints.
     */
    @Bean
    @Order(2)
    public SecurityWebFilterChain securedFilterChain(
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