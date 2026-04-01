package com.group1.apigateway.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.group1.apigateway.common.response.ApiResponse;
import com.group1.apigateway.infrastructure.ratelimit.IpRateLimitProperties;
import com.group1.apigateway.infrastructure.ratelimit.MetadataEffectiveConfigClient;
import com.group1.apigateway.model.dto.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class IpRateLimitWebFilter implements WebFilter {

    @Value("${spring.application.name:api-gateway}")
    private String serviceName;

    @Value("${app.version:1.0.0}")
    private String version;

    private final IpRateLimitProperties props;
    private final MetadataEffectiveConfigClient metadataClient;
    private final ObjectMapper om;

    // ip -> window counter
    private final ConcurrentHashMap<String, WindowCounter> counters = new ConcurrentHashMap<>();

    public IpRateLimitWebFilter(IpRateLimitProperties props,
                                MetadataEffectiveConfigClient metadataClient,
                                ObjectMapper mapper) {
        this.props = props;
        this.metadataClient = metadataClient;
        this.om = mapper.copy().registerModule(new JavaTimeModule());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!props.isEnabled()) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getURI().getPath();

        // Chỉ áp dụng cho /api/**
        if (!path.startsWith("/api/")) {
            return chain.filter(exchange);
        }

        String ip = extractClientIp(exchange);

        return metadataClient.getLimitCached()
                .flatMap(limit -> {
                    int effectiveLimit = limit <= 0 ? props.getDefaultLimit() : limit;
                    boolean allowed = allowRequest(ip, effectiveLimit, props.getWindowSeconds());
                    if (allowed) {
                        return chain.filter(exchange);
                    }
                    log.warn("IP rate limit blocked: ip={}, path={}, limit={}/{}s", ip, path, effectiveLimit, props.getWindowSeconds());
                    return tooManyRequests(exchange, ip, effectiveLimit);
                });
    }

    private boolean allowRequest(String ip, int limit, int windowSeconds) {
        long nowSec = Instant.now().getEpochSecond();
        long windowStart = (nowSec / windowSeconds) * windowSeconds;

        WindowCounter c = counters.compute(ip, (k, old) -> {
            if (old == null || old.windowStartSec != windowStart) {
                return new WindowCounter(windowStart);
            }
            return old;
        });

        int count = c.count.incrementAndGet();
        return count <= limit;
    }

    private String extractClientIp(ServerWebExchange exchange) {
        // Ưu tiên X-Forwarded-For nếu có proxy/load balancer
        String xff = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }

        if (exchange.getRequest().getRemoteAddress() != null
                && exchange.getRequest().getRemoteAddress().getAddress() != null) {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }

        return "unknown";
    }

    private Mono<Void> tooManyRequests(ServerWebExchange exchange, String ip, int limit) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .serviceName(serviceName)
                .version(version)
                .requestId(exchange.getRequest().getId())
                .timestamp(Instant.now())
                .error(ApiError.builder()
                        .code("TOO_MANY_REQUESTS")
                        .message("Too many requests from IP: " + ip
                                + " (limit=" + limit + "/" + props.getWindowSeconds() + "s)")
                        .path(exchange.getRequest().getURI().getPath())
                        .build())
                .build();

        byte[] bytes;
        try {
            bytes = om.writeValueAsBytes(body);
        } catch (Exception e) {
            bytes = ("{\"error\":{\"code\":\"SERIALIZE_ERROR\",\"message\":\"serialize failed\"}}")
                    .getBytes(StandardCharsets.UTF_8);
        }

        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    private static class WindowCounter {
        final long windowStartSec;
        final AtomicInteger count = new AtomicInteger(0);

        WindowCounter(long windowStartSec) {
            this.windowStartSec = windowStartSec;
        }
    }
}
