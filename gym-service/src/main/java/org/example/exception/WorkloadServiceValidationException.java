package org.example.exception;

public class WorkloadServiceValidationException extends IllegalArgumentException {
    public WorkloadServiceValidationException(String msg) {
        super(msg);
    }
}
