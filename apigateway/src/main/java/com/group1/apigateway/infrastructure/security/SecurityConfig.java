package com.group1.apigateway.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
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
                .cors(Customizer.withDefaults())

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/api/auth-service/**", "/auth-service/**").permitAll()
                        .pathMatchers("/api/internal/echo/**").authenticated()
                        .pathMatchers("/api/**").authenticated()
                        .anyExchange().permitAll()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                )

                .addFilterAt(ipRateLimitWebFilter, SecurityWebFiltersOrder.FIRST)
                .addFilterAfter(internalHeaderInjectionWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .build();
    }
}