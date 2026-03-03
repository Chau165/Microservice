package com.group1.metadataservice.repository.RegionOverride;

import com.group1.metadataservice.model.entity.RegionOverride.RegionOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegionOverrideRepository extends JpaRepository<RegionOverride, UUID> {

    Optional<RegionOverride> findByBaseConfig_ConfigKeyAndRegionCode(String key, String regionCode);

    List<RegionOverride> findByRegionCode(String regionCode);
}
