package com.group1.apigateway.infrastructure.security;

import com.group1.apigateway.common.response.ApiResponse;
import com.group1.apigateway.model.dto.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InternalHeaderInjectionWebFilter implements WebFilter {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_ROLE = "X-User-Role";

    @Value("${spring.application.name:api-gateway}")
    private String serviceName;

    @Value("${app.version:1.0.0}")
    private String version;

    private final ObjectMapper objectMapper;

    public InternalHeaderInjectionWebFilter(ObjectMapper mapper) {
        this.objectMapper = mapper.copy().registerModule(new JavaTimeModule());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Skip endpoints that don't need header injection
        if (!path.startsWith("/api/") || path.startsWith("/api/auth/")) {
            return chain.filter(exchange);
        }

        return exchange.getPrincipal()
                .flatMap(principal -> {

                    if (!(principal instanceof JwtAuthenticationToken jwtAuth)) {
                        return chain.filter(exchange);
                    }

                    Map<String, Object> claims = jwtAuth.getToken().getClaims();

                    String userId = extractUserId(claims);
                    String role = extractRole(claims, jwtAuth);

                    if (isBlank(userId)) {
                        return forbidden(exchange, "Missing required claim: userId/sub");
                    }

                    if (isBlank(role)) {
                        return forbidden(exchange, "Missing required claim: role/roles/authorities");
                    }

                    // Inject headers
                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(builder -> builder.headers(headers -> {
                                headers.set(HEADER_USER_ID, userId);
                                headers.set(HEADER_USER_ROLE, role);
                            }))
                            .build();

                    log.info("Injected headers -> {}={}, {}={}, path={}",
                            HEADER_USER_ID, userId,
                            HEADER_USER_ROLE, role,
                            path);

                    return chain.filter(mutatedExchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    // ================== Extract UserId ==================

    private String extractUserId(Map<String, Object> claims) {
        Object uid = claims.get("uid");
        if (uid != null)
            return String.valueOf(uid);

        Object userId = claims.get("userId");
        if (userId != null)
            return String.valueOf(userId);

        Object sub = claims.get("sub");
        return sub != null ? String.valueOf(sub) : null;
    }

    // ================== Extract Role ==================

    private String extractRole(Map<String, Object> claims, JwtAuthenticationToken jwtAuth) {

        // claim: role
        Object role = claims.get("role");
        if (role != null)
            return normalizeRole(String.valueOf(role));

        // claim: roles
        Object roles = claims.get("roles");
        if (roles instanceof Collection<?> col && !col.isEmpty()) {
            return normalizeRole(String.valueOf(col.iterator().next()));
        }

        // claim: authorities
        Object auths = claims.get("authorities");
        if (auths instanceof Collection<?> col && !col.isEmpty()) {
            return normalizeRole(String.valueOf(col.iterator().next()));
        }

        // fallback from GrantedAuthorities
        List<String> authorities = jwtAuth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();

        if (!authorities.isEmpty()) {
            return normalizeRole(authorities.get(0));
        }

        return null;
    }

    private String normalizeRole(String role) {
        if (role == null || role.trim().isEmpty())
            return null;
        String trimmed = role.trim();
        return trimmed.startsWith("ROLE_") ? trimmed.substring(5) : trimmed;
    }

    // ================== 403 Response ==================

    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {

        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .serviceName(serviceName)
                .version(version)
                .requestId(exchange.getRequest().getId())
                .timestamp(Instant.now())
                .error(ApiError.builder()
                        .code("FORBIDDEN")
                        .message(message)
                        .build())
                .build();

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (Exception e) {
            bytes = ("{\"serviceName\":\"" + serviceName + "\",\"version\":\"" + version
                    + "\",\"requestId\":\"unknown\",\"error\":{\"code\":\"SERIALIZE_ERROR\","
                    + "\"message\":\"serialize failed\"}}").getBytes(StandardCharsets.UTF_8);
        }

        log.warn("Header injection failed: {}", message);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(bytes)));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
