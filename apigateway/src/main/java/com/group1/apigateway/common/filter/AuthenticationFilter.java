package com.group1.apigateway.common.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
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
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Slf4j
@Component
public class AuthenticationFilter implements GlobalFilter {

    @Value("${security.jwt.secret}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        log.info("AuthenticationFilter - path: {}, method: {}", path, method);

        // 1. Kiểm tra Whitelist - public paths không cần JWT
        if (isPublicPath(path)) {
            log.info("✓ Public path matched: {}", path);
            return chain.filter(exchange);
        }

        log.warn("⚠ Protected path, checking JWT: {}", path);

        // 2. Kiểm tra Authorization Header cho protected endpoints
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("❌ Missing or invalid Authorization header for path: {}", path);
            return onError(exchange, "Missing or invalid Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            SecretKey key = getSigningKey();

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 3. Trích xuất thông tin từ JWT claims
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            String name = claims.get("name", String.class);
            String permissions = extractPermissions(claims);

            log.debug("✓ Token extracted - userId: {}, role: {}, name: {}, perms: {}", userId, role, name, permissions);

            // Validate required claims
            if (userId == null || userId.isBlank()) {
                log.warn("❌ Missing required claim: subject/userId for path: {}", path);
                return onError(exchange, "Missing required claim: userId", HttpStatus.UNAUTHORIZED);
            }

            // 4. Inject user info vào request headers để service con sử dụng
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role != null ? role : "")
                    .header("X-User-Name", name != null ? name : "")
                    .header("X-User-Permissions", permissions != null ? permissions : "")
                    .build();

            log.info("✓ Token validated for user: {}, role: {}, path: {}", userId, role, path);
            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.error("❌ JWT validation failed for path: {}, exception type: {}, error: {}", 
                    path, e.getClass().getSimpleName(), e.getMessage(), e);
            return onError(exchange, "Token invalid or expired", HttpStatus.UNAUTHORIZED);
        }
    }

    private SecretKey getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey.trim());
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException ex) {
            return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String extractPermissions(Claims claims) {
        Object permissionsClaim = claims.get("permissions");
        if (permissionsClaim instanceof Collection<?> collection) {
            return collection.stream()
                    .map(String::valueOf)
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .reduce((left, right) -> left + "," + right)
                    .orElse("");
        }

        if (permissionsClaim != null) {
            return String.valueOf(permissionsClaim).trim();
        }

        return "";
    }

    private boolean isPublicPath(String path) {
        // Direct string checks - more reliable than pattern matching
        return path.startsWith("/api/auth-service/")
                || path.startsWith("/api/authentication-service/")
                || path.startsWith("/api/public/")
                || path.startsWith("/api/") && (path.contains("/public/") || path.contains("/status"))
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/actuator")
                || path.startsWith("/auth/");
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
}