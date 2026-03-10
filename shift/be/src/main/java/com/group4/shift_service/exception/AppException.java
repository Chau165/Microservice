package com.group4.shift_service.exception;

import lombok.Getter;
import java.util.Map;

@Getter
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, String> errors;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errors = null;
    }

    public AppException(ErrorCode errorCode, String detail) {
        super(String.format(errorCode.getMessage(), detail));
        this.errorCode = errorCode;
        this.errors = null;
    }

    public AppException(ErrorCode errorCode, Map<String, String> errors) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errors = errors;
    }
}