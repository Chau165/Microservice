package com.group1.metadataservice.service;

import com.group1.metadataservice.model.dto.EffectiveConfigDTO;
import com.group1.metadataservice.model.entity.BaseConfig.BaseConfig;
import com.group1.metadataservice.model.entity.RegionOverride.RegionOverride;


public interface ConfigMergeService {

    EffectiveConfigDTO mergeConfigs(BaseConfig baseConfig, RegionOverride regionOverride);

}