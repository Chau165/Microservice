package com.group1.apigateway.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;

@Configuration
public class SecurityConfig {

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
                        .authenticationEntryPoint(authenticationEntryPoint) // 401 JSON
                        .accessDeniedHandler(accessDeniedHandler)           // 403 JSON
                )
                .authorizeExchange(ex -> ex
                        .pathMatchers("/actuator/**").permitAll()

                        // Echo public để test routing (không cần token)
                        .pathMatchers("/api/internal/echo/**").authenticated()

                        .pathMatchers("/api/auth/**").permitAll()
                        // Các API còn lại dưới /api/** bắt buộc phải có JWT hợp lệ
                        .pathMatchers("/api/**").authenticated()

                        .anyExchange().permitAll()
                )
                // JWT validation via JWKS:
                // - đọc Authorization: Bearer <token>
                // - verify signature theo jwk-set-uri
                // - check expiration (exp)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(authenticationEntryPoint) 
                        .jwt(jwt -> {}))
                // Rate limit chạy trước AUTHENTICATION => works without JWT
                .addFilterAt(ipRateLimitWebFilter, SecurityWebFiltersOrder.FIRST)

                // Inject internal headers sau AUTHENTICATION
                .addFilterAfter(internalHeaderInjectionWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
