package org.example.exception;

public class SagaFailedException extends RuntimeException {

    public SagaFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
