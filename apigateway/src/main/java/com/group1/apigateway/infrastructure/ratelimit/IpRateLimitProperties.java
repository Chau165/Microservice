package com.group1.apigateway.infrastructure.ratelimit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "rate-limit.ip")
public class IpRateLimitProperties {

    /**
     * Metadata key để lấy threshold.
     * VD: gateway.ip.rate_limit.per_minute
     */
    private String configKey = "gateway.ip.rate_limit.per_minute";

    /**
     * Fallback khi metadata-service lỗi/timeout
     */
    private int defaultLimit = 60;

    /**
     * Fixed window seconds (VD 60s)
     */
    private int windowSeconds = 60;

    /**
     * Refresh threshold từ metadata mỗi N giây (cache)
     */
    private int refreshSeconds = 15;

    /**
     * Có bật rate limit không
     */
    private boolean enabled = true;
}
