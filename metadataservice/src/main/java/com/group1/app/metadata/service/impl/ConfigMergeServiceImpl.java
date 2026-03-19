package com.group1.app.metadata.service.impl;

import com.group1.app.metadata.dto.EffectiveConfigDTO;
import com.group1.app.metadata.entity.baseconfig.BaseConfig;
import com.group1.app.metadata.entity.regionoverride.RegionOverride;
import com.group1.app.metadata.service.ConfigMergeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigMergeServiceImpl implements ConfigMergeService {

    @Override
    public EffectiveConfigDTO mergeConfigs(
            @NonNull BaseConfig baseConfig,
            RegionOverride regionOverride) {

        // Start with base config values
        EffectiveConfigDTO effective = EffectiveConfigDTO.builder()
                .configKey(baseConfig.getConfigKey())
                .configValue(baseConfig.getConfigValue())
                .configType(baseConfig.getConfigType())
                .configGroup(baseConfig.getConfigGroup())
                .description(baseConfig.getDescription())
                .isOverridden(false)
                .build();

        // Apply override if present and has non-null values
        if (regionOverride != null) {
            if (regionOverride.getOverrideValue() != null) {
                effective.setConfigValue(regionOverride.getOverrideValue());
                effective.setIsOverridden(true);
            }

            if (regionOverride.getEnabled() != null) {
                effective.setEnabled(regionOverride.getEnabled());
            }

            effective.setRegionCode(regionOverride.getRegionCode());
        }

        return effective;
    }

}

