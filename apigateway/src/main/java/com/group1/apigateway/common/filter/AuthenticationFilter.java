package com.group1.apigateway.common.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthenticationFilter implements GlobalFilter {

    @Value("${security.jwt.secret}")
    private String secretKey;

    private final AntPathMatcher matcher = new AntPathMatcher();

    // Danh sách whitelist theo quy ước
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth-service/**",
            "/api/authentication-service/**",
            "/api/public/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api/*-service/**/public/**" // Đây là dòng giải quyết vấn đề "add chay"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 1. Kiểm tra Whitelist
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // 2. Kiểm tra Authorization Header
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            // Cú pháp JJWT 0.12.x mới nhất
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 3. Trích xuất thông tin (Lưu ý: Tên claim phải khớp với lúc bạn tạo Token ở Auth Service)
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            String name = claims.get("name", String.class);
            String permissions = claims.get("permissions", String.class);

            // 4. Inject xuống Service (Sử dụng tên Header khớp với HeaderAuthenticationFilter ở Service con)
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .header("X-User-Name", name != null ? name : "")
                    .header("X-User-Permissions", permissions != null ? permissions : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            return onError(exchange, "Token invalid or expired", HttpStatus.FORBIDDEN);
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(pattern -> matcher.match(pattern, path));
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
}