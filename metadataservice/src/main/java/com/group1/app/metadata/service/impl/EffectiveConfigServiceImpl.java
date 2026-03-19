package com.group1.app.metadata.service.impl;

import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.dto.EffectiveConfigDTO;
import com.group1.app.metadata.entity.baseconfig.BaseConfig;
import com.group1.app.metadata.entity.regionoverride.RegionOverride;
import com.group1.app.metadata.service.BaseConfigService;
import com.group1.app.metadata.service.ConfigMergeService;
import com.group1.app.metadata.service.EffectiveConfigService;
import com.group1.app.metadata.service.RegionOverrideService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<BaseConfig> getAllBaseConfigs(Pageable pageable) {
        return baseService.getAllConfigs(pageable);
    }
}
