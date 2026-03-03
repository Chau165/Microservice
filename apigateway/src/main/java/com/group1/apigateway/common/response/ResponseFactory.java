package com.group1.apigateway.common.response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;

@Component
public class ResponseFactory {

    @Value("${spring.application.name:ApiGateway}")
    private String serviceName;

    @Value("${app.version:1.0.0}")
    private String version;

    public <T> ApiResponse<T> success(T data, ServerWebExchange exchange) {
        return ApiResponse.<T>builder()
                .serviceName(serviceName)
                .version(version)
                .requestId(exchange.getRequest().getId())
                .timestamp(Instant.now())
                .data(data)
                .build();
    }
}