package com.group1.apigateway.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.group1.apigateway.model.dto.ApiError;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String serviceName;
    private String version;
    private String requestId;
    private Instant timestamp;

    private T data;
    private ApiError error;
}
