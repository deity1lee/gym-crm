package org.example.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {

        String transactionId = MDC.get("transactionId");

        LOGGER.error("[{}] Validation/Business error - message = '{}'", transactionId, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<String> handleIllegalArgument(InvalidDataAccessApiUsageException ex) {

        String transactionId = MDC.get("transactionId");

        LOGGER.error("[{}] Validation/Business error - message = '{}'", transactionId, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleUnauthorized(AuthenticationException ex) {

        String transactionId = MDC.get("transactionId");

        LOGGER.error("[{}] Authentication failed - message = '{}'", transactionId, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedException ex) {

        String transactionId = MDC.get("transactionId");

        LOGGER.error("[{}] Authentication failed - message = '{}'", transactionId, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
    }

    @ExceptionHandler(BruteForceAttemptException.class)
    public ResponseEntity<String> handleBruteForceAttempt(BruteForceAttemptException ex) {

        String transactionId = MDC.get("transactionId");

        LOGGER.error("[{}] Brute Force Attempt detected, account is blocked - message = '{}'", transactionId, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.LOCKED)
                .body(ex.getMessage());
    }

    @ExceptionHandler({BadCredentialsException.class})
        public ResponseEntity<String> handleBadCred(BadCredentialsException ex) {

        String transactionId = MDC.get("transactionId");

        LOGGER.error("[{}] Authentication failed - message = '{}'", transactionId, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<String> handleUnsupportedOperation(UnsupportedOperationException ex) {

        String transactionId = MDC.get("transactionId");

        LOGGER.error("[{}] Unsupported operation - message = '{}'", transactionId, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {

        String transactionId = MDC.get("transactionId");

        StringBuilder sb = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> sb.append(fe.getField()).append(" ").append(fe.getDefaultMessage()).append("; "));

        LOGGER.error("[{}] DTO Validation error - message = '{}'", transactionId, sb);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(sb.toString());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {

        String transactionId = MDC.get("transactionId");

        LOGGER.error("[{}] Unexpected error - {}", transactionId, ex.getClass().getName());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected error occurred");
    }

    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<String> handleDownstreamFailure(DownstreamServiceException ex) {
        String transactionId = MDC.get("transactionId");
        LOGGER.error("[{}] {}", transactionId, ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Trainer workload service side error occurred.");
    }

    @ExceptionHandler(SagaFailedException.class)
    public ResponseEntity<String> handleSagaFailure(SagaFailedException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ex.getMessage());
    }
}
