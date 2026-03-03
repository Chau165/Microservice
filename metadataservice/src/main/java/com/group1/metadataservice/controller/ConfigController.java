package com.group1.metadataservice.controller;

import com.group1.metadataservice.common.config.MetadataKeyConfig;
import com.group1.metadataservice.common.exception.ApiException;
import com.group1.metadataservice.common.exception.ErrorCode;
import com.group1.metadataservice.common.response.ApiResponse;
import com.group1.metadataservice.model.dto.EffectiveConfigDTO;
import com.group1.metadataservice.service.EffectiveConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/metadata")
public class ConfigController {

    private final EffectiveConfigService effectiveConfigService;
    private final MetadataKeyConfig metadataKeyConfig;
    private Pattern keyPattern;

    // GET /api/metadata/effective?key=timeout&region=VN
    @GetMapping("/effective")
    public ApiResponse<EffectiveConfigDTO> getEffectiveConfig(
            @RequestParam String key,
            @RequestParam(required = false) String region
    ) {
        // Validate key format
        if (key == null || key.isBlank()) {
            throw new ApiException(ErrorCode.INVALID_KEY,
                    "Metadata key cannot be null or empty. Must match pattern: " + metadataKeyConfig.getKeyPattern());
        }

        // Lazy compile pattern from config
        if (keyPattern == null) {
            keyPattern = Pattern.compile(metadataKeyConfig.getKeyPattern());
        }

        if (!keyPattern.matcher(key).matches()) {
            throw new ApiException(ErrorCode.INVALID_KEY,
                    "Invalid metadata key format: " + key + ". Must match pattern: " + metadataKeyConfig.getKeyPattern());
        }

        return ApiResponse.success(
                effectiveConfigService.getEffectiveConfig(key, region)
        );
    }
}
