package com.group1.app.metadata.repository.baseconfig;

import com.group1.app.metadata.entity.baseconfig.BaseConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BaseConfigRepository extends JpaRepository<BaseConfig, UUID> {

    Optional<BaseConfig> findByConfigKey(String key);

    List<BaseConfig> findByConfigGroup(String group);

    boolean existsByConfigKey(String configKey);
}
