package com.group1.app.metadata.controller;

import com.group1.app.common.config.MetadataKeyConfig;
import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.metadata.dto.EffectiveConfigDTO;
import com.group1.app.metadata.service.EffectiveConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("ConfigController - Metadata Key Validation Tests")
@ExtendWith(MockitoExtension.class)
class ConfigControllerKeyValidationTest {

    @Mock
    private EffectiveConfigService effectiveConfigService;

    private ConfigController controller;

    @BeforeEach
    void setUp() {
        MetadataKeyConfig metadataKeyConfig = new MetadataKeyConfig();
        metadataKeyConfig.setKeyPattern("^[A-Z_]+$");
        controller = new ConfigController(effectiveConfigService, metadataKeyConfig);
    }

    @Test
    @DisplayName("Valid key: DATABASE_URL - should call service")
    void testValidKey_ShouldCallService() {
        // Arrange
        String validKey = "DATABASE_URL";
        String region = "US-EAST-1";
        EffectiveConfigDTO expectedDto = EffectiveConfigDTO.builder()
                .configKey(validKey)
                .configValue("jdbc:sqlserver://...")
                .build();

        when(effectiveConfigService.getEffectiveConfig(validKey, region))
                .thenReturn(expectedDto);

        // Act
        var result = controller.getEffectiveConfig(validKey, region);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(expectedDto, result.getData());
        verify(effectiveConfigService).getEffectiveConfig(validKey, region);
    }

    @Test
    @DisplayName("Invalid key: database_url (lowercase) - should throw exception")
    void testInvalidKey_Lowercase_ShouldThrowException() {
        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () ->
                controller.getEffectiveConfig("database_url", "US-EAST-1")
        );

        assertEquals(ErrorCode.INVALID_KEY, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Invalid metadata key format"));
        assertTrue(exception.getMessage().contains("^[A-Z_]+$"));
        verify(effectiveConfigService, never()).getEffectiveConfig(anyString(), anyString());
    }

    @Test
    @DisplayName("Invalid key: api-token (hyphen) - should throw exception")
    void testInvalidKey_WithHyphen_ShouldThrowException() {
        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () ->
                controller.getEffectiveConfig("api-token", "US-EAST-1")
        );

        assertEquals(ErrorCode.INVALID_KEY, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Must match pattern"));
        verify(effectiveConfigService, never()).getEffectiveConfig(anyString(), anyString());
    }

    @Test
    @DisplayName("Invalid key: api.token (dot) - should throw exception")
    void testInvalidKey_WithDot_ShouldThrowException() {
        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () ->
                controller.getEffectiveConfig("api.token", "US-EAST-1")
        );

        assertEquals(ErrorCode.INVALID_KEY, exception.getErrorCode());
        verify(effectiveConfigService, never()).getEffectiveConfig(anyString(), anyString());
    }

    @Test
    @DisplayName("Invalid key: MAX_123 (with numbers) - should throw exception")
    void testInvalidKey_WithNumbers_ShouldThrowException() {
        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () ->
                controller.getEffectiveConfig("MAX_123", "US-EAST-1")
        );

        assertEquals(ErrorCode.INVALID_KEY, exception.getErrorCode());
        verify(effectiveConfigService, never()).getEffectiveConfig(anyString(), anyString());
    }

    @Test
    @DisplayName("Null key - should throw exception")
    void testNullKey_ShouldThrowException() {
        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () ->
                controller.getEffectiveConfig(null, "US-EAST-1")
        );

        assertEquals(ErrorCode.INVALID_KEY, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("cannot be null or empty"));
        verify(effectiveConfigService, never()).getEffectiveConfig(anyString(), anyString());
    }

    @Test
    @DisplayName("Empty key - should throw exception")
    void testEmptyKey_ShouldThrowException() {
        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () ->
                controller.getEffectiveConfig("", "US-EAST-1")
        );

        assertEquals(ErrorCode.INVALID_KEY, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("cannot be null or empty"));
        verify(effectiveConfigService, never()).getEffectiveConfig(anyString(), anyString());
    }

    @Test
    @DisplayName("Whitespace key - should throw exception")
    void testWhitespaceKey_ShouldThrowException() {
        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () ->
                controller.getEffectiveConfig("   ", "US-EAST-1")
        );

        assertEquals(ErrorCode.INVALID_KEY, exception.getErrorCode());
        verify(effectiveConfigService, never()).getEffectiveConfig(anyString(), anyString());
    }

    @Test
    @DisplayName("Valid key without region - should call service with null region")
    void testValidKey_NoRegion_ShouldCallService() {
        // Arrange
        String validKey = "CONFIG_VALUE";
        EffectiveConfigDTO expectedDto = EffectiveConfigDTO.builder()
                .configKey(validKey)
                .configValue("some_value")
                .build();

        when(effectiveConfigService.getEffectiveConfig(validKey, null))
                .thenReturn(expectedDto);

        // Act
        var result = controller.getEffectiveConfig(validKey, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(effectiveConfigService).getEffectiveConfig(validKey, null);
    }

    @Test
    @DisplayName("Error message includes pattern rule")
    void testErrorMessage_IncludesPatternRule() {
        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () ->
                controller.getEffectiveConfig("invalid-key", null)
        );

        String message = exception.getMessage();
        assertTrue(message.contains("Invalid metadata key format"));
        assertTrue(message.contains("invalid-key"));
        assertTrue(message.contains("^[A-Z_]+$"));
    }
}
