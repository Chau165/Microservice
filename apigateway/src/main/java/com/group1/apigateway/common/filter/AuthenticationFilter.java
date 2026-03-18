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
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuthenticationFilter implements GlobalFilter {

    @Value("${security.jwt.secret}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        String requestId = exchange.getRequest().getId();

        log.info("AuthenticationFilter - requestId: {}, path: {}, method: {}", requestId, path, method);

        // 1. Kiểm tra Whitelist - public paths không cần JWT
        if (isPublicPath(path)) {
            log.info("✓ Public path matched: {}, requestId: {}", path, requestId);
            return chain.filter(exchange);
        }

        log.warn("⚠ Protected path, checking JWT: {}, requestId: {}", path, requestId);

        // 2. Kiểm tra Authorization Header cho protected endpoints
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("❌ Missing/invalid Authorization header, path: {}, requestId: {}, authHeaderPresent: {}",
                    path, requestId, authHeader != null);
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
            String userId = extractUserId(claims);
            String role = extractRole(claims);
            String name = extractName(claims);
            String permissions = extractPermissions(claims);

            log.info("JWT claims extracted - requestId: {}, path: {}, sub: {}, userIdClaim: {}, role: {}, hasName: {}, permissionsCount: {}, claimKeys: {}",
                    requestId,
                    path,
                    claims.getSubject(),
                    toText(claims.get("userId")),
                    role,
                    StringUtils.hasText(name),
                    countPermissions(permissions),
                    claims.keySet());

            // Validate required claims
            if (!StringUtils.hasText(userId)) {
                log.warn("❌ Missing required claim: userId/sub, path: {}, requestId: {}", path, requestId);
                return onError(exchange, "Missing required claim: userId", HttpStatus.UNAUTHORIZED);
            }
            if (!StringUtils.hasText(role)) {
                log.warn("❌ Missing required claim: role/roles/authorities, path: {}, requestId: {}", path, requestId);
                return onError(exchange, "Missing required claim: role", HttpStatus.UNAUTHORIZED);
            }

            // 4. Inject user info vào request headers để service con sử dụng
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .header("X-User-Name", name != null ? name : "")
                    .header("X-User-Permissions", permissions != null ? permissions : "")
                    .build();

            log.info("✓ Token validated and headers injected, requestId: {}, userId: {}, role: {}, path: {}",
                    requestId, userId, role, path);

            return chain.filter(exchange.mutate().request(mutatedRequest).build())
                    .doOnSuccess(unused -> log.info("Downstream response - requestId: {}, path: {}, status: {}",
                            requestId,
                            path,
                            exchange.getResponse().getStatusCode()))
                    .doOnError(error -> log.error("Downstream processing failed - requestId: {}, path: {}, type: {}, message: {}",
                            requestId,
                            path,
                            error.getClass().getSimpleName(),
                            error.getMessage(),
                            error));

        } catch (Exception e) {
            log.error("❌ JWT validation failed for path: {}, exception type: {}, error: {}", 
                    path, e.getClass().getSimpleName(), e.getMessage(), e);
            return onError(exchange, "Token invalid or expired", HttpStatus.UNAUTHORIZED);
        }
    }

    private String extractUserId(Claims claims) {
        String userId = toText(claims.get("userId"));
        if (StringUtils.hasText(userId)) {
            return userId;
        }

        String subject = claims.getSubject();
        if (StringUtils.hasText(subject)) {
            return subject;
        }

        Object uid = claims.get("uid");
        return toText(uid);
    }

    private String extractRole(Claims claims) {
        String role = toText(claims.get("role"));
        if (StringUtils.hasText(role)) {
            return normalizeRole(role);
        }

        Object roles = claims.get("roles");
        if (roles instanceof Collection<?> collection) {
            String fromRoles = firstRoleValue(collection);
            if (StringUtils.hasText(fromRoles)) {
                return normalizeRole(fromRoles);
            }
        }

        Object authorities = claims.get("authorities");
        if (authorities instanceof Collection<?> collection) {
            String fromAuthorities = firstRoleValue(collection);
            if (StringUtils.hasText(fromAuthorities)) {
                return normalizeRole(fromAuthorities);
            }
        }

        return null;
    }

    private String extractName(Claims claims) {
        String name = toText(claims.get("name"));
        if (StringUtils.hasText(name)) {
            return name;
        }

        String displayName = toText(claims.get("displayName"));
        if (StringUtils.hasText(displayName)) {
            return displayName;
        }

        String username = toText(claims.get("username"));
        if (StringUtils.hasText(username)) {
            return username;
        }

        return toText(claims.getSubject());
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
        List<String> permissions = new ArrayList<>();

        if (permissionsClaim instanceof Collection<?> collection) {
            collection.stream()
                    .map(String::valueOf)
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .forEach(permissions::add);
        } else if (permissionsClaim != null) {
            String value = String.valueOf(permissionsClaim).trim();
            if (StringUtils.hasText(value)) {
                for (String part : value.split(",")) {
                    String trimmed = part == null ? "" : part.trim();
                    if (StringUtils.hasText(trimmed)) {
                        permissions.add(trimmed);
                    }
                }
            }
        }

        Object authoritiesClaim = claims.get("authorities");
        if (authoritiesClaim instanceof Collection<?> collection) {
            collection.stream()
                    .map(String::valueOf)
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .filter(value -> !value.startsWith("ROLE_"))
                    .forEach(permissions::add);
        }

        return permissions.stream().distinct().collect(Collectors.joining(","));
    }

    private String firstRoleValue(Collection<?> values) {
        for (Object value : values) {
            String text = toText(value);
            if (!StringUtils.hasText(text)) {
                continue;
            }
            if (text.startsWith("ROLE_")) {
                return text;
            }
            return text;
        }
        return null;
    }

    private String normalizeRole(String role) {
        if (!StringUtils.hasText(role)) {
            return null;
        }
        String trimmed = role.trim();
        if (trimmed.startsWith("ROLE_")) {
            return trimmed.substring(5);
        }
        return trimmed;
    }

    private String toText(Object value) {
        return value == null ? null : String.valueOf(value).trim();
    }

    private int countPermissions(String permissionsCsv) {
        if (!StringUtils.hasText(permissionsCsv)) {
            return 0;
        }
        return (int) java.util.Arrays.stream(permissionsCsv.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .count();
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