package org.example.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {

        String transactionId = MDC.get("transactionId");

        LOGGER.error("[{}] Validation/Business error - message = '{}'", transactionId, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedException ex) {

        String transactionId = MDC.get("transactionId");

        LOGGER.error("[{}] Authentication failed - message = '{}'", transactionId, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
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

        LOGGER.error("[{}] Unexpected error - ", transactionId, ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected error occurred");
    }
}
