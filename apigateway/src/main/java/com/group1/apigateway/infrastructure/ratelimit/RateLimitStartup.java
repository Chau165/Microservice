package com.group1.apigateway.infrastructure.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RateLimitStartup {

    private final MetadataEffectiveConfigClient metadataClient;
    private final IpRateLimitProperties props;

    @Bean
    public ApplicationRunner rateLimitRefreshRunner() {
        return args -> {
            if (!props.isEnabled()) {
                log.info("IP rate limit is disabled (rate-limit.ip.enabled=false)");
                return;
            }

            log.info(
                    "IP rate limit bootstrap config: key={}, defaultLimit={}, windowSeconds={}, refreshSeconds={}",
                    props.getConfigKey(),
                    props.getDefaultLimit(),
                    props.getWindowSeconds(),
                    props.getRefreshSeconds()
            );

            metadataClient.refreshLimitOnce()
                    .doOnNext(limit -> log.info(
                            "IP rate limit effective at startup: {} requests / {}s",
                            limit,
                            props.getWindowSeconds()
                    ))
                    .subscribe();

            metadataClient.startPeriodicRefresh().subscribe();
        };
    }
}
