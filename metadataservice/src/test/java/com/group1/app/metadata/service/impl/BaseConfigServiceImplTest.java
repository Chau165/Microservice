package com.group1.app.metadata.service.impl;

import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.entity.baseconfig.BaseConfig;
import com.group1.app.metadata.repository.baseconfig.BaseConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseConfigServiceImplTest {

    @InjectMocks
    BaseConfigServiceImpl service;

    @Mock
    BaseConfigRepository repo;

    @Test
    void getByKey_found() {
        BaseConfig cfg = new BaseConfig();
        cfg.setConfigKey("k");
        when(repo.findByConfigKey("k")).thenReturn(Optional.of(cfg));

        BaseConfig res = service.getByKey("k");
        assertEquals("k", res.getConfigKey());
    }

    @Test
    void getByKey_notFound() {
        when(repo.findByConfigKey("k")).thenReturn(Optional.empty());
        ApiException ex = assertThrows(ApiException.class, () -> service.getByKey("k"));
        assertEquals(ErrorCode.CONFIG_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getAllConfigs_delegates() {
        Page<BaseConfig> page = new PageImpl<>(java.util.List.of());
        when(repo.findAll(PageRequest.of(0, 10))).thenReturn(page);
        Page<BaseConfig> res = service.getAllConfigs(PageRequest.of(0, 10));
        assertNotNull(res);
    }

    @Test
    void update_success() {
        BaseConfig cfg = new BaseConfig(); cfg.setConfigKey("k");
        when(repo.findByConfigKey("k")).thenReturn(Optional.of(cfg));

        service.update("k", "v");

        assertEquals("v", cfg.getConfigValue());
    }

    @Test
    void update_optimisticLock_conflict() {
        when(repo.findByConfigKey("k")).thenThrow(new org.springframework.orm.ObjectOptimisticLockingFailureException(BaseConfig.class, "1"));

        ApiException ex = assertThrows(ApiException.class, () -> service.update("k", "v"));
        assertEquals(ErrorCode.CONFIG_CONFLICT, ex.getErrorCode());
    }
}

