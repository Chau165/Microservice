package com.group1.metadataservice.service.Impl;

import com.group1.metadataservice.common.exception.ApiException;
import com.group1.metadataservice.common.exception.ErrorCode;
import com.group1.metadataservice.model.dto.EffectiveConfigDTO;
import com.group1.metadataservice.model.entity.BaseConfig.BaseConfig;
import com.group1.metadataservice.model.entity.RegionOverride.RegionOverride;
import com.group1.metadataservice.service.BaseConfigService;
import com.group1.metadataservice.service.ConfigMergeService;
import com.group1.metadataservice.service.EffectiveConfigService;
import com.group1.metadataservice.service.RegionOverrideService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EffectiveConfigServiceImpl implements EffectiveConfigService {
    private final BaseConfigService baseService;
    private final RegionOverrideService regionService;
    private final ConfigMergeService mergeService;
    private static final Pattern REGION_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+$");

    @Override
    @Cacheable(value = "effective-config", key = "#key + '-' + #regionCode")
    public EffectiveConfigDTO getEffectiveConfig(String key, String regionCode) {

        if (regionCode != null && !regionCode.isBlank()) {
            if (!REGION_PATTERN.matcher(regionCode).matches()) {
                throw new ApiException(ErrorCode.INVALID_REGION, "Invalid region: " + regionCode);
            }
        }

        BaseConfig base = baseService.getByKey(key);

        RegionOverride override = null;

        if (regionCode != null) {
            override = regionService
                    .getByKeyAndRegion(key, regionCode)
                    .orElse(null);
        }

        return mergeService.mergeConfigs(base, override);
    }
}
