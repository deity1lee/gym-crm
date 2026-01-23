package org.example.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.entities.Trainee;
import org.example.entities.User;
import org.example.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final Counter successfulLoginCounter;

    public AuthController(AuthService authService, MeterRegistry meterRegistry) {
        this.authService = authService;
        this.successfulLoginCounter = Counter.builder("gym_successful_logins_total")
                .description("Total number of successful logins")
                .register(meterRegistry);
    }
    @Operation(
            summary = "Login user",
            description = "Authenticates a user with provided credentials"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials or validation error"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public String login(
            @RequestHeader("X-Auth-Username") String username,
            @RequestHeader("X-Auth-Password") String password) {

        String txId = MDC.get("transactionId");

        LOGGER.info("[{}] GET /auth - login(username={})", txId, username);

        try {
            User user = authService.authenticateUser(username, password);
            String role = (user instanceof Trainee) ? "Trainee" : "Trainer";

            LOGGER.info("[{}] login success - 200 OK - username={}, role={}", txId, user.getUsername(), role);
            successfulLoginCounter.increment();
            return "Login successful";
        } catch (Exception ex) {
            LOGGER.error("[{}] login failed - 400 BAD REQUEST - username={}", txId, username);
            throw ex;
        }
    }
}
