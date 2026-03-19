package com.group1.app.common.exception;

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
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_999", "Internal server error"),

    // ===== CONTRACT =====
    CT_001_CONTRACT_NOT_FOUND(HttpStatus.NOT_FOUND, "CT_001", "Contract not found"),
    CT_002_ACTIVE_CONTRACT_EXISTS(HttpStatus.BAD_REQUEST, "CT_002", "Active contract already exists"),
    CT_003_INVALID_CONTRACT_DATE(HttpStatus.BAD_REQUEST, "CT_003", "Invalid contract date range"),
    CT_004_DUPLICATE_CONTRACT_NUMBER(HttpStatus.BAD_REQUEST, "CT_004", "Duplicate contract number"),
    CT_005_INVALID_ROYALTY_RATE(HttpStatus.BAD_REQUEST, "CT_005", "Invalid royalty rate"),
    CT_006_INVALID_CONTRACT_STATUS(HttpStatus.BAD_REQUEST, "CT_006", "Invalid contract status"),
    CT_007_CONTRACT_DATE_OVERLAP(HttpStatus.BAD_REQUEST, "CT_007", "Contract date overlaps with existing contract"),

    // ===== FRANCHISE =====
    FR_404_FRANCHISE_NOT_FOUND(HttpStatus.NOT_FOUND, "FR_404", "Franchise not found"),

    FRANCHISE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "FR_006", "Franchise code already exists"),
    INVALID_TIMEZONE(HttpStatus.BAD_REQUEST, "FR_007", "Timezone is not supported"),
    OPERATIONAL_CONFIG_INCOMPLETE(HttpStatus.BAD_REQUEST, "FR_008", "Operational configuration incomplete"),
    CANNOT_ACTIVATE_NO_ACTIVE_CONTRACT(HttpStatus.BAD_REQUEST, "FR_009", "Cannot activate franchise without active contract"),
    INVALID_FRANCHISE_STATUS(HttpStatus.CONFLICT, "FR_010", "Invalid franchise status for this operation"),
    FRANCHISE_ALREADY_HAS_OWNER(HttpStatus.CONFLICT, "FR_011", "Franchise already has an active owner"),
    INVALID_EFFECTIVE_DATE(HttpStatus.BAD_REQUEST, "FR_012", "Effective date cannot be in the past"),
    FRANCHISE_HAS_NO_OWNER(HttpStatus.BAD_REQUEST, "FR_409_NO_OWNER", "Franchise does not have an owner"),
    INVALID_OWNER_TRANSFER(HttpStatus.BAD_REQUEST, "FR_409_OWNER_TRANSFER", "New owner must be different from current owner"),

    // ===== OPENING HOURS =====
    OH_001_BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "OH_001", "Brand not found for franchise"),
    OH_002_INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST, "OH_002", "Close time must be after open time"),
    OH_003_EXCEEDS_MAX_HOURS(HttpStatus.BAD_REQUEST, "OH_003", "Opening hours exceed brand maximum hours per day"),

    // ===== FRANCHISE STAFF =====
    FS_001_STAFF_NOT_FOUND(HttpStatus.NOT_FOUND, "FS_001", "Staff not found"),
    FS_002_STAFF_ALREADY_ASSIGNED(HttpStatus.CONFLICT, "FS_002", "Staff already assigned to a franchise"),
    FS_003_FRANCHISE_SUSPENDED(HttpStatus.CONFLICT, "FS_003", "Franchise is suspended"),
    FS_004_MAPPING_NOT_FOUND(HttpStatus.NOT_FOUND, "FS_004", "Franchise-staff mapping not found"),
    FS_005_INVALID_STAFF_ID(HttpStatus.BAD_REQUEST, "FS_005", "Invalid staff id"),
    FS_006_STAFF_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "FS_006", "Staff service unavailable"),

    // ===== WAREHOUSE MAPPING =====
    WM_001_WAREHOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, "WM_001", "Warehouse not found"),
    WM_002_WAREHOUSE_MAPPING_NOT_FOUND(HttpStatus.NOT_FOUND, "WM_002", "Warehouse mapping not found"),
    WM_003_ACTIVE_WAREHOUSE_MAPPING_EXISTS(HttpStatus.CONFLICT, "WM_003", "Active warehouse mapping already exists"),
    WM_004_WAREHOUSE_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "WM_004", "Warehouse service unavailable"),

    // ==== SUPPLIER MAPPING ====
    SUPPLIER_NOT_FOUND(HttpStatus.NOT_FOUND, "SP_001", "Supplier not found"),
    SUPPLIER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "SP_002", "Supplier service unavailable");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
