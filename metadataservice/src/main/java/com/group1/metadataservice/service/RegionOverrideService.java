package com.group1.metadataservice.service;

import com.group1.metadataservice.model.entity.RegionOverride.RegionOverride;

import java.util.Optional;

public interface RegionOverrideService {
    void update(String key, String regionCode, String value);

    Optional<RegionOverride> getByKeyAndRegion(String key, String regionCode);
}
