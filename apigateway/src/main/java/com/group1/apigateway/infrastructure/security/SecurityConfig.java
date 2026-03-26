package com.group1.apigateway.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class SecurityConfig {

    @Value("${security.jwt.secret}")
    private String secretString;

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        byte[] keyBytes = Base64.getDecoder().decode(secretString);
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");

        return NimbusReactiveJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ServerAuthenticationEntryPoint authenticationEntryPoint,
            ServerAccessDeniedHandler accessDeniedHandler,
            InternalHeaderInjectionWebFilter internalHeaderInjectionWebFilter,
            IpRateLimitWebFilter ipRateLimitWebFilter,
            Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter
    ) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeExchange(ex -> ex

                        // ================= PUBLIC =================
                        .pathMatchers(
                                "/api/auth/**",
                                "/api/auth-service/**",
                                "/api/authentication-service/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/*/public/**",
                                "/api/public/**",
                                "/api/*-service/public/**",
                                "/api/*-service/*/public/**",
                                "/api/franchise-service/franchises/public"
                        ).permitAll()

                        // ================= CART (GUEST) =================
                        .pathMatchers(HttpMethod.GET, "/api/products/cart").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/products/cart/add").permitAll()
                        .pathMatchers(HttpMethod.PUT, "/api/products/cart/item").permitAll()
                        .pathMatchers(HttpMethod.DELETE, "/api/products/cart/item/*").permitAll()

                        // fallback (nếu có endpoint khác)
                        .pathMatchers("/api/products/cart", "/api/products/cart/**").permitAll()

                        // ================= INVOICE =================
                        .pathMatchers(HttpMethod.POST,
                                "/api/products/invoices/*/points",
                                "/api/products/invoices/*/coupon",
                                "/api/products/invoices/*/checkout",
                                "/api/products/invoices/create/*"
                        ).permitAll()

                        .pathMatchers(HttpMethod.GET,
                                "/api/products/invoices",
                                "/api/products/invoices/*"
                        ).permitAll()

                        // ================= PRODUCT PUBLIC =================
                        .pathMatchers(HttpMethod.GET, "/api/products").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/products/*").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/products/*/variants").permitAll()

                        // ================= DEFAULT =================
                        .pathMatchers("/api/**").authenticated()
                        .anyExchange().permitAll()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                )

                // ================= FILTER =================
                .addFilterAt(ipRateLimitWebFilter, SecurityWebFiltersOrder.FIRST)
                .addFilterAfter(internalHeaderInjectionWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .build();
    }
}
