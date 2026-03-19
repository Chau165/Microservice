package com.group1.app.metadata.data;

import com.group1.app.metadata.entity.baseconfig.BaseConfig;
import com.group1.app.metadata.entity.baseconfig.ConfigType;
import com.group1.app.metadata.repository.baseconfig.BaseConfigRepository;
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
