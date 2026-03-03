package com.group1.franchiseservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    SUCCESS(1000, "Success", HttpStatus.OK),
    CREATED(1001, "Created successfully", HttpStatus.CREATED),

    VALIDATION_ERROR(4000, "Validation failed", HttpStatus.BAD_REQUEST),
    MALFORMED_JSON(4005, "Malformed JSON request", HttpStatus.BAD_REQUEST),

    CONTRACT_ALREADY_EXISTS(4001, "Contract number already exists", HttpStatus.BAD_REQUEST),
    INVALID_DATE_RANGE(4002, "Start date must be before end date", HttpStatus.BAD_REQUEST),
    ACTIVE_CONTRACT_OVERLAP(4003, "Another active contract already exists for this franchise", HttpStatus.BAD_REQUEST),

    CONTRACT_NOT_FOUND(4041, "Contract not found", HttpStatus.NOT_FOUND),
    FRANCHISE_NOT_FOUND(4042, "Franchise does not exist", HttpStatus.NOT_FOUND),

    CONTRACT_NOT_ACTIVE(4091, "Only ACTIVE contracts can be renewed", HttpStatus.CONFLICT),
    CONTRACT_ALREADY_TERMINATED(4092, "Contract is already terminated", HttpStatus.CONFLICT),

    INVALID_NEW_END_DATE(4004, "New end date must be greater than current end date", HttpStatus.BAD_REQUEST),

    FORBIDDEN(4030, "Forbidden", HttpStatus.FORBIDDEN),
    UNEXPECTED_ERROR(5000, "Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String defaultMessage;
    private final HttpStatus status;
}