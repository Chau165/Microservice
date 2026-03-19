package com.group1.app.metadata.service;

import com.group1.app.metadata.dto.EffectiveConfigDTO;
import com.group1.app.metadata.entity.baseconfig.BaseConfig;
import com.group1.app.metadata.entity.baseconfig.ConfigType;
import com.group1.app.metadata.entity.regionoverride.RegionOverride;
import com.group1.app.metadata.service.impl.ConfigMergeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConfigMergeService Tests")
class ConfigMergeServiceTest {

    private ConfigMergeServiceImpl configMergeServiceImpl;
    private BaseConfig baseConfig;

    @BeforeEach
    void setUp() {
        configMergeServiceImpl = new ConfigMergeServiceImpl();

        // Create a test base config
        baseConfig = BaseConfig.builder()
                .id(UUID.randomUUID())
                .configKey("app.database.url")
                .configValue("jdbc:sqlserver://localhost:1433;databaseName=basedb")
                .configType(ConfigType.DATABASE)
                .configGroup("database")
                .description("Database connection string")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Scenario 1: No override - should return base config values")
    void testMergeWithNoOverride() {
        // Act
        EffectiveConfigDTO effective = configMergeServiceImpl.mergeConfigs(baseConfig, null);

        // Assert
        assertNotNull(effective);
        assertEquals("app.database.url", effective.getConfigKey());
        assertEquals("jdbc:sqlserver://localhost:1433;databaseName=basedb", effective.getConfigValue());
        assertEquals(ConfigType.DATABASE, effective.getConfigType());
        assertEquals("database", effective.getConfigGroup());
        assertEquals("Database connection string", effective.getDescription());
        assertFalse(effective.getIsOverridden());
        assertNull(effective.getRegionCode());
        assertNull(effective.getEnabled());
    }

    @Test
    @DisplayName("Scenario 2: Partial override - only override value is replaced")
    void testMergeWithPartialOverride() {
        // Arrange
        RegionOverride regionOverride = RegionOverride.builder()
                .id(UUID.randomUUID())
                .baseConfig(baseConfig)
                .regionCode("US-EAST-1")
                .overrideValue("jdbc:sqlserver://us-east-1.db:1433;databaseName=regionaldb")
                .enabled(null)  // Not overridden
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Act
        EffectiveConfigDTO effective = configMergeServiceImpl.mergeConfigs(baseConfig, regionOverride);

        // Assert
        assertNotNull(effective);
        assertEquals("app.database.url", effective.getConfigKey());
        assertEquals("jdbc:sqlserver://us-east-1.db:1433;databaseName=regionaldb", effective.getConfigValue());
        assertEquals(ConfigType.DATABASE, effective.getConfigType());
        assertEquals("database", effective.getConfigGroup());
        assertEquals("Database connection string", effective.getDescription());
        assertTrue(effective.getIsOverridden());
        assertEquals("US-EAST-1", effective.getRegionCode());
        assertNull(effective.getEnabled());
    }

    @Test
    @DisplayName("Scenario 3: Full override - both value and enabled are overridden")
    void testMergeWithFullOverride() {
        // Arrange
        RegionOverride regionOverride = RegionOverride.builder()
                .id(UUID.randomUUID())
                .baseConfig(baseConfig)
                .regionCode("EU-WEST-1")
                .overrideValue("jdbc:sqlserver://eu-west-1.db:1433;databaseName=eudb")
                .enabled(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Act
        EffectiveConfigDTO effective = configMergeServiceImpl.mergeConfigs(baseConfig, regionOverride);

        // Assert
        assertNotNull(effective);
        assertEquals("app.database.url", effective.getConfigKey());
        assertEquals("jdbc:sqlserver://eu-west-1.db:1433;databaseName=eudb", effective.getConfigValue());
        assertEquals(ConfigType.DATABASE, effective.getConfigType());
        assertEquals("database", effective.getConfigGroup());
        assertEquals("Database connection string", effective.getDescription());
        assertTrue(effective.getIsOverridden());
        assertEquals("EU-WEST-1", effective.getRegionCode());
        assertFalse(effective.getEnabled());
    }

    @Test
    @DisplayName("Base config remains immutable after merge")
    void testBaseConfigImmutable() {
        // Arrange
        String originalValue = baseConfig.getConfigValue();
        UUID originalId = baseConfig.getId();
        RegionOverride regionOverride = RegionOverride.builder()
                .id(UUID.randomUUID())
                .baseConfig(baseConfig)
                .regionCode("ASIA-1")
                .overrideValue("jdbc:sqlserver://asia.db:1433;databaseName=asiadb")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Act
        configMergeServiceImpl.mergeConfigs(baseConfig, regionOverride);

        // Assert - Base config should remain unchanged
        assertEquals(originalValue, baseConfig.getConfigValue());
        assertEquals(originalId, baseConfig.getId());
        assertEquals("app.database.url", baseConfig.getConfigKey());
    }

    @Test
    @DisplayName("Should throw NullPointerException when baseConfig is null")
    void testMergeWithNullBaseConfigThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            configMergeServiceImpl.mergeConfigs(null, null);
        });
    }

    @Test
    @DisplayName("Override with null overrideValue should not override configValue")
    void testPartialOverrideWithNullOverrideValue() {
        // Arrange
        RegionOverride regionOverride = RegionOverride.builder()
                .id(UUID.randomUUID())
                .baseConfig(baseConfig)
                .regionCode("APAC")
                .overrideValue(null)  // No value override
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Act
        EffectiveConfigDTO effective = configMergeServiceImpl.mergeConfigs(baseConfig, regionOverride);

        // Assert
        assertEquals("jdbc:sqlserver://localhost:1433;databaseName=basedb", effective.getConfigValue());
        assertFalse(effective.getIsOverridden());
        assertTrue(effective.getEnabled());
        assertEquals("APAC", effective.getRegionCode());
    }
}
