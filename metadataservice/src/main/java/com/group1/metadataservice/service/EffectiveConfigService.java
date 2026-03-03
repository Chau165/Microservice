package com.group1.metadataservice.service;

import com.group1.metadataservice.model.dto.EffectiveConfigDTO;

public interface EffectiveConfigService {
    EffectiveConfigDTO getEffectiveConfig(String key, String regionCode);
}
