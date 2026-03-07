package com.group5.engagement.exception;

import com.group5.engagement.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex) {

        ApiResponse<Void> res = ApiResponse.error(ex.getMessage(), ex.getCode());
        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(InsufficientPointsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientPoints(
            InsufficientPointsException ex
    ) {
        return ResponseEntity.badRequest().body(
                ApiResponse.error(
                        ex.getMessage(),
                        "INSUFFICIENT_POINTS"
                )
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            ResourceNotFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error(
                        ex.getMessage(),
                        "RESOURCE_NOT_FOUND"
                )
        );
    }
}
