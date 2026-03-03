package com.group1.apigateway.infrastructure.ratelimit;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetadataEffectiveConfigClient {

    private final WebClient.Builder webClientBuilder;
    private final IpRateLimitProperties props;

    private final AtomicInteger cachedLimit = new AtomicInteger(-1);

    /**
     * Trả limit đã cache. Nếu chưa có thì fetch ngay lần đầu.
     */
    public Mono<Integer> getLimitCached() {
        if (!props.isEnabled()) {
            return Mono.just(Integer.MAX_VALUE);
        }

        int current = cachedLimit.get();
        if (current > 0) {
            return Mono.just(current);
        }

        // cache chưa có -> fetch ngay
        return refreshLimitOnce()
                .onErrorReturn(props.getDefaultLimit());
    }

    /**
     * Gọi metadata-service 1 lần, lấy limit và update cache.
     */
    public Mono<Integer> refreshLimitOnce() {
        String key = props.getConfigKey();

        return webClientBuilder.build()
                .get()
                .uri("lb://METADATA-SERVICE/api/metadata/effective?key={key}", key)
                .retrieve()
                .bodyToMono(MetadataApiResponse.class)
                .map(resp -> {
                    if (resp == null || resp.getData() == null) {
                        throw new IllegalStateException("metadata response invalid");
                    }
                    String value = resp.getData().getConfigValue();
                    int limit = Integer.parseInt(value.trim());
                    cachedLimit.set(limit);
                    return limit;
                })
                .timeout(Duration.ofSeconds(2))
                .doOnSuccess(limit -> log.info("IP rate limit loaded from metadata: key={}, limit={}", key, limit))
                .doOnError(e -> log.warn("Failed to load IP rate limit from metadata (fallback={}): {}",
                        props.getDefaultLimit(), e.toString()))
                .onErrorResume(e -> {
                    int fallback = props.getDefaultLimit();
                    // Lưu fallback vào cache để các request tiếp theo không gọi lại metadata
                    cachedLimit.compareAndSet(-1, fallback);
                    return Mono.just(fallback);
                });
    }

    /**
     * Periodic refresh loop — gọi 1 lần khi app start, refresh theo interval.
     * onErrorResume để 1 lần fail không kill toàn bộ interval stream.
     */
    public Mono<Void> startPeriodicRefresh() {
        if (!props.isEnabled()) return Mono.empty();

        return reactor.core.publisher.Flux
                .interval(Duration.ofSeconds(5), Duration.ofSeconds(props.getRefreshSeconds()))
                .flatMap(tick -> refreshLimitOnce()
                        .onErrorResume(e -> {
                            log.warn("Periodic refresh failed, keeping cached limit: {}", cachedLimit.get());
                            return Mono.empty();
                        }))
                .then();
    }

    // ===== DTO map JSON từ metadata-service =====

    @Data
    public static class MetadataApiResponse {
        private boolean success;
        private String message;
        private EffectiveConfigDTO data;
        private Object error;
        private String timestamp;
    }

    @Data
    public static class EffectiveConfigDTO {
        private String configKey;
        private String configValue;
        private String configType;
        private String configGroup;
        private String description;
        private String regionCode;
        private Boolean enabled;
        private Boolean isOverridden;
    }
}
