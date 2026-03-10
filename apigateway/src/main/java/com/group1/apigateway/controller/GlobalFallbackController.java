package com.group1.apigateway.controller;

import com.group1.apigateway.common.response.ApiResponse;
import com.group1.apigateway.model.dto.ApiError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.time.Instant;

@RestController
@RequestMapping("/fallback")
public class GlobalFallbackController {

    @Value("${spring.application.name:ApiGateway}")
    private String serviceName;

    @Value("${app.version:1.0.0}")
    private String version;

    @RequestMapping("/{targetService}")
    public ResponseEntity<ApiResponse<Void>> fallback(
            @PathVariable String targetService,
            ServerHttpRequest request) {

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .serviceName(serviceName)
                .version(version)
                .requestId(request.getId())
                .timestamp(Instant.now())
                .error(ApiError.builder()
                        .code("SERVICE_UNAVAILABLE")
                        .message(targetService + " is currently unavailable")
                        .path(request.getPath().value())
                        .build())
                .build();

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}