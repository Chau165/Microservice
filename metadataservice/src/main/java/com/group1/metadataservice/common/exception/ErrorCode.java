package com.group1.metadataservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    CONFIG_CONFLICT(HttpStatus.CONFLICT, "CONFIG_002", "Config was modified by another user"),
    CONFIG_NOT_FOUND(HttpStatus.NOT_FOUND, "CONFIG_001", "Configuration not found"),
    INVALID_REGION(HttpStatus.BAD_REQUEST, "REGION_001", "Invalid region"),
    INVALID_KEY(HttpStatus.BAD_REQUEST, "KEY_001", "Invalid metadata key format"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_999", "Internal server error");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
