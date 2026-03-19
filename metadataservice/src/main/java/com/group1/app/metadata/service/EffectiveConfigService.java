package com.group1.app.metadata.service;

import com.group1.app.metadata.dto.EffectiveConfigDTO;
import com.group1.app.metadata.entity.baseconfig.BaseConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EffectiveConfigService {
    EffectiveConfigDTO getEffectiveConfig(String key, String regionCode);

    Page<BaseConfig> getAllBaseConfigs(Pageable pageable);
}
