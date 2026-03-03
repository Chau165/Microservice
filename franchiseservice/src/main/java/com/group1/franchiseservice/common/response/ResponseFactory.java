package com.group1.franchiseservice.common.response;

import com.group1.franchiseservice.common.filter.RequestIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ResponseFactory {

    @Value("${spring.application.name:franchise-service}")
    private String serviceName;

    @Value("${app.version:1.0}")
    private String version;

    public <T> ApiResponse<T> success(
            int code,
            String message,
            T data,
            HttpServletRequest request) {

        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .result(data)
                .service(serviceName)
                .version(version)
                .requestId((String) request.getAttribute(RequestIdFilter.REQUEST_ID))
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();
    }

    public ApiResponse<Object> error(
            int code,
            String message,
            HttpServletRequest request,
            java.util.Map<String, String> errors) {

        return ApiResponse.builder()
                .code(code)
                .message(message)
                .result(null)
                .service(serviceName)
                .version(version)
                .requestId((String) request.getAttribute(RequestIdFilter.REQUEST_ID))
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .errors(errors)
                .build();
    }
}