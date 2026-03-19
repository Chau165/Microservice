package com.group1.app.common.util;

import com.group1.app.metadata.service.EffectiveConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class MetadataHelper {

    private final EffectiveConfigService effectiveConfigService;

    public int getInt(String key, String context, int defaultValue) {
        try {
            String value = effectiveConfigService.getEffectiveConfig(key, context).getConfigValue();
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public BigDecimal getDecimal(String key, String context, BigDecimal defaultValue) {
        try {
            String value = effectiveConfigService.getEffectiveConfig(key, context).getConfigValue();
            return new BigDecimal(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, String context, boolean defaultValue) {
        try {
            String value = effectiveConfigService.getEffectiveConfig(key, context).getConfigValue();
            return Boolean.parseBoolean(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}