package com.group1.app.metadata.controller;

import com.group1.app.common.config.MetadataKeyConfig;
import com.group1.app.common.exception.ApiException;
import com.group1.app.common.exception.ErrorCode;
import com.group1.app.common.response.ApiResponse;
import com.group1.app.common.response.PageResponse;
import com.group1.app.metadata.dto.EffectiveConfigDTO;
import com.group1.app.metadata.service.EffectiveConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.regex.Pattern;

@RestController
@RequestMapping("api/metadata")
public class ConfigController {

	private final EffectiveConfigService effectiveConfigService;
	private final MetadataKeyConfig metadataKeyConfig;
	private Pattern keyPattern;

	public ConfigController(EffectiveConfigService effectiveConfigService, MetadataKeyConfig metadataKeyConfig) {
		this.effectiveConfigService = effectiveConfigService;
		this.metadataKeyConfig = metadataKeyConfig;
	}

	// GET /api/metadata/effective?key=timeout&region=VN
	@GetMapping("/effective")
	@PreAuthorize("hasRole('ADMIN')")
//	@PreAuthorize("hasAuthority('METADATA_CONFIG_VIEW')")
	public ApiResponse<EffectiveConfigDTO> getEffectiveConfig(
			@RequestParam String key,
			@RequestParam(required = false) String region
	) {
		// Validate key presence
		if (key == null || key.isBlank()) {
			throw new ApiException(ErrorCode.INVALID_KEY, "Invalid metadata key format: cannot be null or empty");
		}

		// Lazy compile pattern from config
		if (keyPattern == null) {
			keyPattern = Pattern.compile(metadataKeyConfig.getKeyPattern());
		}

		if (!keyPattern.matcher(key).matches()) {
			String message = String.format("Invalid metadata key format: '%s' Must match pattern %s",
					key, metadataKeyConfig.getKeyPattern());
			throw new ApiException(ErrorCode.INVALID_KEY, message);
		}

		return ApiResponse.success(
				effectiveConfigService.getEffectiveConfig(key, region)
		);
	}

	@GetMapping
	@PreAuthorize("hasAuthority('METADATA_CONFIG_VIEW')")
	public ApiResponse<PageResponse<?>> getAllMetadata(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) {

		if (page < 0) page = 0;
		if (size <= 0) size = 10;
		if (size > 100) size = 100;

		Pageable pageable = PageRequest.of(
				page,
				size,
				Sort.by("createdAt").descending()
		);

		Page<?> result = effectiveConfigService.getAllBaseConfigs(pageable);

		return ApiResponse.success(
				PageResponse.from(result)
		);
	}
}