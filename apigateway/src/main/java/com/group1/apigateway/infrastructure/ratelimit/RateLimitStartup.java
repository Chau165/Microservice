package com.group1.apigateway.infrastructure.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RateLimitStartup {

    private final MetadataEffectiveConfigClient metadataClient;

    @Bean
    public ApplicationRunner rateLimitRefreshRunner() {
        return args -> metadataClient.startPeriodicRefresh().subscribe();
    }
}
