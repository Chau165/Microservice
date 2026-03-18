package com.group1.apigateway.common.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class AuthenticationFilter implements GlobalFilter {

    @Value("${security.jwt.secret}")
    private String secretKey;

    private final AntPathMatcher matcher = new AntPathMatcher();

    // Danh sách whitelist - các endpoint không cần JWT token
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth-service/**",
            "/api/authentication-service/**",
            "/api/public/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api/*-service/**/public/**"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        log.debug("AuthenticationFilter - path: {}, method: {}", path, method);

        // 1. Kiểm tra Whitelist - public paths không cần JWT
        if (isPublicPath(path)) {
            log.debug("Public path matched: {}", path);
            return chain.filter(exchange);
        }

        // 2. Kiểm tra Authorization Header cho protected endpoints
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return onError(exchange, "Missing or invalid Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            // Cú pháp JJWT 0.12.x
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 3. Trích xuất thông tin từ JWT claims
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            String name = claims.get("name", String.class);
            String permissions = claims.get("permissions", String.class);

            log.debug("Token validated for user: {}, role: {}, path: {}", userId, role, path);

            // 4. Inject user info vào request headers để service con sử dụng
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .header("X-User-Name", name != null ? name : "")
                    .header("X-User-Permissions", permissions != null ? permissions : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.error("JWT validation failed for path: {}, error: {}", path, e.getMessage());
            return onError(exchange, "Token invalid or expired", HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(pattern -> matcher.match(pattern, path));
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
}