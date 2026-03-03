package com.group1.franchiseservice.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int code;
    private String message;
    private T result;

    // ===== API METADATA =====
    private String service;
    private String version;
    private String requestId;

    private String path;
    private Instant timestamp;

    // ===== DATA METADATA =====
    private PageMeta page;
    private Map<String, String> errors;

    @Data
    @Builder
    public static class PageMeta {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
    }
}