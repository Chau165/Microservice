package com.group1.app.metadata.service;

import com.group1.app.metadata.dto.EffectiveConfigDTO;
import com.group1.app.metadata.entity.baseconfig.BaseConfig;
import com.group1.app.metadata.entity.regionoverride.RegionOverride;


public interface ConfigMergeService {

    EffectiveConfigDTO mergeConfigs(BaseConfig baseConfig, RegionOverride regionOverride);

}