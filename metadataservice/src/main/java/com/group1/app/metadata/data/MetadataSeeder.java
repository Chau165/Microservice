package com.group1.app.metadata.data;

import com.group1.app.metadata.entity.baseconfig.BaseConfig;
import com.group1.app.metadata.entity.baseconfig.ConfigType;
import com.group1.app.metadata.repository.baseconfig.BaseConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MetadataSeeder implements CommandLineRunner {

    private final BaseConfigRepository repository;

    @Override
    public void run(String... args) {

        seedIfMissing("CONTRACT_ROYALTY_MAX","100",
                "Maximum allowable royalty rate percentage");

        seedIfMissing("CONTRACT_ROYALTY_DEFAULT","1.0",
                "Default royalty rate if not provided by client");

        seedIfMissing("CONTRACT_DURATION_MAX_YEARS","100",
                "Maximum duration allowed for a single contract");

        seedIfMissing("CONTRACT_RENEW_MAX_YEARS","100",
                "Maximum extension period allowed during renewal");

        seedIfMissing("CONTRACT_AUTO_ORDER_DEFAULT","true",
                "Default value for auto order enabled flag");

        seedIfMissing("CONTRACT_ACTIVE_LIMIT_PER_FRANCHISE","1",
                "Maximum active contracts per franchise");
    }

    private void seedIfMissing(String key,String value,String description){

        if(!repository.existsByConfigKey(key)){

            repository.save(
                    BaseConfig.builder()
                            .configKey(key)
                            .configValue(value)
                            .configType(ConfigType.STRING)
                            .description(description)
                            .build()
            );
        }
    }
}