package com.group1.metadataservice.bootstrap;

import com.group1.metadataservice.model.entity.BaseConfig.BaseConfig;
import com.group1.metadataservice.model.entity.BaseConfig.ConfigType;
import com.group1.metadataservice.repository.BaseConfig.BaseConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DefaultConfigSeeder {

    private final BaseConfigRepository baseConfigRepository;

    @Bean
    public ApplicationRunner seedDefaultConfigs() {
        return args -> {
            seedIfMissing(
                    "gateway.ip.rate_limit.per_minute",
                    "Rate limit per IP per minute for API Gateway",
                    "GATEWAY_RATE_LIMIT",
                    ConfigType.NUMBER,
                    "60"
            );
        };
    }

    private void seedIfMissing(String key,
                               String description,
                               String group,
                               ConfigType type,
                               String value) {

        if (baseConfigRepository.existsByConfigKey(key)) {
            log.info("BaseConfig already exists, skipping seed: key={}", key);
            return;
        }

        BaseConfig cfg = BaseConfig.builder()
                .configKey(key)
                .configType(type)
                .configGroup(group)
                .configValue(value)
                .description(description)
                .build();

        baseConfigRepository.save(cfg);
        log.info("Seeded BaseConfig: key={}, value={}", key, value);
    }
}
