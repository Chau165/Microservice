package com.group1.franchiseservice.common.exception;

import com.group1.franchiseservice.common.response.ResponseFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ResponseFactory responseFactory;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(
            ApiException ex,
            HttpServletRequest request) {

        ErrorCode ec = ex.getErrorCode();

        return ResponseEntity.status(ec.getStatus())
                .body(responseFactory.error(
                        ec.getCode(),
                        ex.getMessage(),
                        request,
                        null
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }

        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getStatus())
                .body(responseFactory.error(
                        ErrorCode.VALIDATION_ERROR.getCode(),
                        ErrorCode.VALIDATION_ERROR.getDefaultMessage(),
                        request,
                        errors
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleMalformed(HttpServletRequest request) {

        return ResponseEntity.status(ErrorCode.MALFORMED_JSON.getStatus())
                .body(responseFactory.error(
                        ErrorCode.MALFORMED_JSON.getCode(),
                        ErrorCode.MALFORMED_JSON.getDefaultMessage(),
                        request,
                        null
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(HttpServletRequest request) {

        return ResponseEntity.status(ErrorCode.FORBIDDEN.getStatus())
                .body(responseFactory.error(
                        ErrorCode.FORBIDDEN.getCode(),
                        ErrorCode.FORBIDDEN.getDefaultMessage(),
                        request,
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnexpected(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error", ex);

        return ResponseEntity.status(ErrorCode.UNEXPECTED_ERROR.getStatus())
                .body(responseFactory.error(
                        ErrorCode.UNEXPECTED_ERROR.getCode(),
                        ErrorCode.UNEXPECTED_ERROR.getDefaultMessage(),
                        request,
                        null
                ));
    }
}