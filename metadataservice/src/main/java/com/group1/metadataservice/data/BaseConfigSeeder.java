package com.group1.metadataservice.data;

import com.group1.metadataservice.model.entity.BaseConfig.BaseConfig;
import com.group1.metadataservice.model.entity.BaseConfig.ConfigType;
import com.group1.metadataservice.repository.BaseConfig.BaseConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BaseConfigSeeder implements ApplicationRunner {

    private final BaseConfigRepository repository;

    @Override
    public void run(ApplicationArguments args) {

        if (repository.count() > 0) return;

        repository.saveAll(List.of(

                BaseConfig.builder()
                        .configKey("system.timezone")
                        .configValue("Asia/Ho_Chi_Minh")
                        .configType(ConfigType.STRING)
                        .configGroup("SYSTEM")
                        .description("Default system timezone")
                        .build(),

                BaseConfig.builder()
                        .configKey("feature.review.enabled")
                        .configValue("true")
                        .configType(ConfigType.BOOLEAN)
                        .configGroup("FEATURE")
                        .description("Enable review feature")
                        .build()
        ));
    }
}
