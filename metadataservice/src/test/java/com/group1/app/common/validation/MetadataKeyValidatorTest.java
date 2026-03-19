package com.group1.app.common.validation;

import com.group1.app.common.config.MetadataKeyConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("MetadataKeyValidator Tests")
class MetadataKeyValidatorTest {
    @Autowired
    private MetadataKeyValidator validator;
    private MetadataKeyConfig metadataKeyConfig;

    @BeforeEach
    void setUp() {
        metadataKeyConfig = new MetadataKeyConfig();
        metadataKeyConfig.setKeyPattern("^[A-Z_]+$");
        validator = new MetadataKeyValidator(metadataKeyConfig);
        validator.initialize(null);
    }

    @Test
    @DisplayName("Valid key: DATABASE_URL")
    void testValidKey_DatabaseUrl() {
        boolean result = validator.isValid("DATABASE_URL", null);
        assertTrue(result);
    }

    @Test
    @DisplayName("Valid key: API_TOKEN")
    void testValidKey_ApiToken() {
        boolean result = validator.isValid("API_TOKEN", null);
        assertTrue(result);
    }

    @Test
    @DisplayName("Valid key: MAX_CONNECTIONS")
    void testValidKey_MaxConnections() {
        boolean result = validator.isValid("MAX_CONNECTIONS", null);
        assertTrue(result);
    }

    @Test
    @DisplayName("Valid key: Single underscore")
    void testValidKey_Underscore() {
        boolean result = validator.isValid("_", null);
        assertTrue(result);
    }

    @Test
    @DisplayName("Invalid key: lowercase letters")
    void testInvalidKey_Lowercase() {
        boolean result = validator.isValid("database_url", null);
        assertFalse(result);
    }

    @Test
    @DisplayName("Invalid key: mixed case")
    void testInvalidKey_MixedCase() {
        boolean result = validator.isValid("Database_URL", null);
        assertFalse(result);
    }

    @Test
    @DisplayName("Invalid key: contains hyphen")
    void testInvalidKey_WithHyphen() {
        boolean result = validator.isValid("DATABASE-URL", null);
        assertFalse(result);
    }

    @Test
    @DisplayName("Invalid key: contains dot")
    void testInvalidKey_WithDot() {
        boolean result = validator.isValid("DATABASE.URL", null);
        assertFalse(result);
    }

    @Test
    @DisplayName("Invalid key: contains numbers")
    void testInvalidKey_WithNumbers() {
        boolean result = validator.isValid("DATABASE123", null);
        assertFalse(result);
    }

    @Test
    @DisplayName("Invalid key: contains space")
    void testInvalidKey_WithSpace() {
        boolean result = validator.isValid("DATABASE URL", null);
        assertFalse(result);
    }

    @Test
    @DisplayName("Null value is valid (optional field)")
    void testNullValue_IsValid() {
        boolean result = validator.isValid(null, null);
        assertTrue(result);
    }

    @Test
    @DisplayName("Empty string is valid (blank check)")
    void testEmptyString_IsValid() {
        boolean result = validator.isValid("", null);
        assertTrue(result);
    }

    @Test
    @DisplayName("Whitespace only is valid (blank check)")
    void testWhitespaceOnly_IsValid() {
        boolean result = validator.isValid("   ", null);
        assertTrue(result);
    }

    @Test
    @DisplayName("Starts with number - invalid")
    void testInvalidKey_StartsWithNumber() {
        boolean result = validator.isValid("123_DATABASE", null);
        assertFalse(result);
    }
}
