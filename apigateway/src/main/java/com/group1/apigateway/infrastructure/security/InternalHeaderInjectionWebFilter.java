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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InternalHeaderInjectionWebFilter implements WebFilter {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_ROLE = "X-User-Role";
    public static final String HEADER_USER_NAME = "X-User-Name";
    public static final String HEADER_USER_PERMISSIONS = "X-User-Permissions";

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
        if (!path.startsWith("/api/") || path.startsWith("/api/auth/") || path.startsWith("/api/auth-service/")) {
            return chain.filter(exchange);
        }

        return exchange.getPrincipal()
                .flatMap(principal -> {

                    if (!(principal instanceof JwtAuthenticationToken)) {
                        return chain.filter(exchange);
                    }

                    JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) principal;

                    Map<String, Object> claims = jwtAuth.getToken().getClaims();

                    String userId = extractUserId(claims);
                    String role = extractRole(claims, jwtAuth);
                    String name = extractName(claims);
                    String permissions = extractPermissions(claims, jwtAuth);

                    if (isBlank(userId)) {
                        return forbidden(exchange, "Missing required claim: userId/sub");
                    }

                    if (isBlank(role)) {
                        return forbidden(exchange, "Missing required claim: role/roles/authorities");
                    }

                    if (isBlank(name)) {
                        return forbidden(exchange, "Missing required claim: name");
                    }

                    // Inject headers
                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(builder -> builder.headers(headers -> {
                                headers.set(HEADER_USER_ID, sanitizeHeaderValue(userId));
                                headers.set(HEADER_USER_ROLE, sanitizeHeaderValue(role));
                                headers.set(HEADER_USER_NAME, sanitizeHeaderValue(name));
                                headers.set(HEADER_USER_PERMISSIONS, sanitizeHeaderValue(permissions));
                            }))
                            .build();

                        log.info("Injected headers -> {}={}, {}={}, permsCount={}, path={}",
                            HEADER_USER_ID, userId,
                            HEADER_USER_ROLE, role,
                            permissions.isBlank() ? 0 : permissions.split(",").length,
                            path);

                    return chain.filter(mutatedExchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    // ================== Extract Name ==================

    private String extractName(Map<String, Object> claims) {

        Object name = claims.get("name");
        if (!isBlank(toText(name))) {
            return toText(name);
        }

        Object displayName = claims.get("displayName");
        if (!isBlank(toText(displayName))) {
            return toText(displayName);
        }

        Object username = claims.get("username");
        if (!isBlank(toText(username))) {
            return toText(username);
        }

        Object preferredUsername = claims.get("preferred_username");
        if (!isBlank(toText(preferredUsername))) {
            return toText(preferredUsername);
        }

        Object sub = claims.get("sub");
        return isBlank(toText(sub)) ? null : toText(sub);
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
        if (roles instanceof Collection<?> && !((Collection<?>) roles).isEmpty()) {
            Collection<?> col = (Collection<?>) roles;
            String fromClaims = findFirstRole(col);
            if (!isBlank(fromClaims)) {
                return fromClaims;
            }
        }

        // claim: authorities
        Object auths = claims.get("authorities");
        if (auths instanceof Collection<?> && !((Collection<?>) auths).isEmpty()) {
            Collection<?> col = (Collection<?>) auths;
            String fromClaims = findFirstRole(col);
            if (!isBlank(fromClaims)) {
                return fromClaims;
            }
        }

        // fallback from GrantedAuthorities
        List<String> authorities = jwtAuth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();

        if (!authorities.isEmpty()) {
            String fromAuthorities = findFirstRole(authorities);
            if (!isBlank(fromAuthorities)) {
                return fromAuthorities;
            }
        }

        return null;
    }

    private String extractPermissions(Map<String, Object> claims, JwtAuthenticationToken jwtAuth) {
        List<String> rawAuthorities = new ArrayList<>();

        Object roles = claims.get("roles");
        if (roles instanceof Collection<?>) {
            Collection<?> col = (Collection<?>) roles;
            col.forEach(value -> rawAuthorities.add(toText(value)));
        }

        Object authorities = claims.get("authorities");
        if (authorities instanceof Collection<?>) {
            Collection<?> col = (Collection<?>) authorities;
            col.forEach(value -> rawAuthorities.add(toText(value)));
        }

        jwtAuth.getAuthorities().forEach(a -> rawAuthorities.add(toText(a.getAuthority())));

        return rawAuthorities.stream()
                .filter(value -> !isBlank(value))
                .map(String::trim)
                .filter(value -> !value.startsWith("ROLE_"))
                .distinct()
                .collect(Collectors.joining(","));
    }

    private String findFirstRole(Collection<?> values) {
        return values.stream()
                .map(this::toText)
                .filter(value -> !isBlank(value))
                .filter(value -> value.startsWith("ROLE_"))
                .map(this::normalizeRole)
                .findFirst()
                .orElse(null);
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

    private String toText(Object value) {
        return value == null ? null : String.valueOf(value).trim();
    }

    private String sanitizeHeaderValue(String value) {
        return value == null ? "" : value.replace("\r", "").replace("\n", "").trim();
    }
}
