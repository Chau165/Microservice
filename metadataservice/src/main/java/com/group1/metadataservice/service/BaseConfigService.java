package com.group1.metadataservice.service;

import com.group1.metadataservice.model.entity.BaseConfig.BaseConfig;

public interface BaseConfigService {
    void update(String key, String value);

    BaseConfig getByKey(String key);
}
