package com.group1.app.metadata.service.impl;

import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.entity.baseconfig.BaseConfig;
import com.group1.app.metadata.repository.baseconfig.BaseConfigRepository;
import com.group1.app.metadata.service.BaseConfigService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BaseConfigServiceImpl implements BaseConfigService {
    private final BaseConfigRepository repo;

    @Override
    @Transactional
    @CacheEvict(value = "effective-config", allEntries = true)
    public void update(String key, String value) {

        try {
            BaseConfig config = repo.findByConfigKey(key)
                    .orElseThrow(() -> new ApiException(ErrorCode.CONFIG_NOT_FOUND));

            config.setConfigValue(value);

        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new ApiException(ErrorCode.CONFIG_CONFLICT);
        }
    }

    @Override
    public BaseConfig getByKey(String key) {
        return repo.findByConfigKey(key)
                .orElseThrow(() -> new ApiException(ErrorCode.CONFIG_NOT_FOUND));
    }

    @Override
    public Page<BaseConfig> getAllConfigs(Pageable pageable) {
        return repo.findAll(pageable);
    }
}
