package com.group1.app.metadata.service.impl;

import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.dto.EffectiveConfigDTO;
import com.group1.app.metadata.entity.baseconfig.BaseConfig;
import com.group1.app.metadata.service.BaseConfigService;
import com.group1.app.metadata.service.ConfigMergeService;
import com.group1.app.metadata.service.RegionOverrideService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EffectiveConfigServiceImplTest {

    @InjectMocks
    EffectiveConfigServiceImpl service;

    @Mock
    BaseConfigService baseService;

    @Mock
    RegionOverrideService regionService;

    @Mock
    ConfigMergeService mergeService;

    @Test
    void getEffectiveConfig_validRegion_callsMerge() {
        BaseConfig base = new BaseConfig();
        base.setConfigKey("k");
        base.setConfigValue("v");

        when(baseService.getByKey("k")).thenReturn(base);
        when(regionService.getByKeyAndRegion("k", "r")).thenReturn(Optional.empty());
        when(mergeService.mergeConfigs(base, null)).thenReturn(EffectiveConfigDTO.builder().configKey("k").configValue("v").build());

        EffectiveConfigDTO dto = service.getEffectiveConfig("k", "r");

        assertNotNull(dto);
        assertEquals("k", dto.getConfigKey());
    }

    @Test
    void getEffectiveConfig_invalidRegion_throws() {
        ApiException ex = assertThrows(ApiException.class, () -> service.getEffectiveConfig("k", "!bad"));
        assertEquals(ErrorCode.INVALID_REGION, ex.getErrorCode());
    }
}

