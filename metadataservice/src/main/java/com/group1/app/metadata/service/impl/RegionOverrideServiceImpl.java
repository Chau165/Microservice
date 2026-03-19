package com.group1.app.metadata.service.impl;

import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.entity.regionoverride.RegionOverride;
import com.group1.app.metadata.repository.baseconfig.BaseConfigRepository;
import com.group1.app.metadata.repository.regionoverride.RegionOverrideRepository;
import com.group1.app.metadata.service.RegionOverrideService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegionOverrideServiceImpl implements RegionOverrideService {

    private final RegionOverrideRepository repo;
    private final BaseConfigRepository baseRepo;

    @Override
    @Transactional
    @CacheEvict(value = "effective-config", allEntries = true)
    public void update(String key, String regionCode, String value) {

        try {
            RegionOverride override = repo
                    .findByBaseConfig_ConfigKeyAndRegionCode(key, regionCode)
                    .orElseThrow(() -> new ApiException(ErrorCode.CONFIG_NOT_FOUND));

            override.setOverrideValue(value);

        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new ApiException(ErrorCode.CONFIG_CONFLICT);
        }
    }

    @Override
    public Optional<RegionOverride> getByKeyAndRegion(String key, String regionCode) {
        return repo.findByBaseConfig_ConfigKeyAndRegionCode(key, regionCode);
    }
}
