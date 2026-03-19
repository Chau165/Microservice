package com.group1.app.metadata.service;

import com.group1.app.metadata.entity.baseconfig.BaseConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseConfigService {
    void update(String key, String value);

    BaseConfig getByKey(String key);

    Page<BaseConfig> getAllConfigs(Pageable pageable);
}