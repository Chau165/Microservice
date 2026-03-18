package com.group1.apigateway.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            InternalHeaderInjectionWebFilter internalHeaderInjectionWebFilter,
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
                        .anyExchange().permitAll()
                )

                .addFilterAt(ipRateLimitWebFilter, SecurityWebFiltersOrder.FIRST)
                .addFilterAfter(internalHeaderInjectionWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .build();
    }
}