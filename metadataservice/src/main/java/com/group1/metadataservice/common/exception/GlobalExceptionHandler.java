package com.group1.metadataservice.common.exception;

import com.group1.metadataservice.common.response.ApiError;
import com.group1.metadataservice.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> handleApiException(
            ApiException ex,
            HttpServletRequest req
    ) {

        ErrorCode errorCode = ex.getErrorCode();

        ApiError error = ApiError.builder()
                .code(errorCode.getCode())
                .message(ex.getMessage())
                .path(req.getRequestURI())
                .build();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.builder()
                        .success(false)
                        .error(error)
                        .timestamp(Instant.now())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleUnhandled(
            Exception ex,
            HttpServletRequest req
    ) {
        log.error("Unhandled exception processing request {}", req.getRequestURI(), ex);

        ApiError error = ApiError.builder()
                .code(ErrorCode.INTERNAL_ERROR.getCode())
                .message(ex.getMessage() != null ? ex.getMessage() : ErrorCode.INTERNAL_ERROR.getMessage())
                .path(req.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.builder()
                        .success(false)
                        .error(error)
                        .timestamp(Instant.now())
                        .build());
    }
}
