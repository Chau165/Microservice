package com.group1.app.metadata.service.impl;

import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.entity.regionoverride.RegionOverride;
import com.group1.app.metadata.repository.baseconfig.BaseConfigRepository;
import com.group1.app.metadata.repository.regionoverride.RegionOverrideRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegionOverrideServiceImplTest {

    @InjectMocks
    RegionOverrideServiceImpl service;

    @Mock
    RegionOverrideRepository repo;

    @Mock
    BaseConfigRepository baseRepo;

    @Test
    void getByKeyAndRegion_delegates() {
        when(repo.findByBaseConfig_ConfigKeyAndRegionCode("k", "r")).thenReturn(Optional.empty());
        Optional<RegionOverride> res = service.getByKeyAndRegion("k", "r");
        assertTrue(res.isEmpty());
    }

    @Test
    void update_optimisticLock_throwsApiException() {
        doThrow(new ObjectOptimisticLockingFailureException("x", null))
                .when(repo).findByBaseConfig_ConfigKeyAndRegionCode("k", "r");

        ApiException ex = assertThrows(ApiException.class, () -> service.update("k", "r", "v"));
        assertEquals(ErrorCode.CONFIG_CONFLICT, ex.getErrorCode());
    }
}

