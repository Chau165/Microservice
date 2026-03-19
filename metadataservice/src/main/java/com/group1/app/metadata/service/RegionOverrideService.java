package com.group1.app.metadata.service;

import com.group1.app.metadata.entity.regionoverride.RegionOverride;

import java.util.Optional;

public interface RegionOverrideService {
    void update(String key, String regionCode, String value);

    Optional<RegionOverride> getByKeyAndRegion(String key, String regionCode);
}
