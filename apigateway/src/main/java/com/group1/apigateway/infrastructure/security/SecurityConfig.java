package com.group1.apigateway.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            InternalHeaderInjectionWebFilter internalHeaderInjectionWebFilter,
            org.springframework.core.convert.converter.Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter,
            IpRateLimitWebFilter ipRateLimitWebFilter
    ) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())

                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/api/auth-service/**").permitAll()
                        .pathMatchers("/api/authentication-service/**").permitAll()
                        .pathMatchers("/api/public/**").permitAll()
                        .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyExchange().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                )

                // Bật lại sau khi metadata-service ổn định
                // .addFilterAt(ipRateLimitWebFilter, SecurityWebFiltersOrder.FIRST)

                .addFilterAfter(internalHeaderInjectionWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .build();
    }
}