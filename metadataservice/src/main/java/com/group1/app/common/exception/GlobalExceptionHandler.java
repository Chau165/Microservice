package com.group1.app.common.exception;

import com.group1.app.common.response.ApiError;
import com.group1.app.common.response.ApiResponse;
import com.group1.app.shift.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---- Metadata domain exceptions ----
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> handleApiException(ApiException ex, HttpServletRequest req) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiError error = ApiError.builder()
                .code(errorCode.getCode())
                .message(ex.getMessage())
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.<Object>builder()
                        .success(false).message(ex.getMessage()).error(error).timestamp(Instant.now()).build());
    }

    // ---- Shift domain exceptions ----
    @ExceptionHandler(AppException.class)
    public ResponseEntity<com.group1.app.shift.dto.response.ApiResponse<Object>> handleAppException(AppException ex) {
        Map<String, Object> body = new HashMap<>();
        if (ex.getErrors() != null) {
            body.putAll(ex.getErrors());
        }
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
                .body(com.group1.app.shift.dto.response.ApiResponse.builder()
                        .code(ex.getErrorCode().getCode())
                        .message(ex.getMessage())
                        .result(ex.getErrors())
                        .build());
    }

    // ---- Validation (shared) ----
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ApiError error = ApiError.builder().code("VALIDATION_ERROR").message(message).path(req.getRequestURI()).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Object>builder()
                        .success(false).message(message).error(error).timestamp(Instant.now()).build());
    }

    // ---- Fallback ----
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        ApiError error = ApiError.builder()
                .code("ACCESS_DENIED")
                .message(ex.getMessage())
                .path(req.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.<Object>builder()
                        .success(false).message(ex.getMessage()).error(error).timestamp(Instant.now()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleUnhandled(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception processing request {}", req.getRequestURI(), ex);
        String message = ex.getMessage() != null ? ex.getMessage() : ErrorCode.INTERNAL_ERROR.getMessage();
        ApiError error = ApiError.builder()
                .code(ErrorCode.INTERNAL_ERROR.getCode()).message(message).path(req.getRequestURI()).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Object>builder()
                        .success(false).message(message).error(error).timestamp(Instant.now()).build());
    }
}
