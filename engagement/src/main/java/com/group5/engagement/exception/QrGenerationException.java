package com.group5.engagement.exception;

public class QrGenerationException extends RuntimeException {

    public QrGenerationException(String message) {
        super("QR_GENERATION_ERROR: " + message);
    }
}
