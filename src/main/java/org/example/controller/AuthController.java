package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.example.dto.ChangePasswordRequest;
import org.example.entities.Trainee;
import org.example.entities.User;
import org.example.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Authentication")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ApiOperation(
            value = "Login user",
            notes = "Authenticates a user with provided credentials"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Login successful"),
            @ApiResponse(code = 400, message = "Invalid credentials or validation error"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<String> login(@RequestHeader("X-Auth-Username") String username,
                                        @RequestHeader("X-Auth-Password") String password) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] GET /auth - login(username={})", txId, username);

        try {
            User user = authService.authenticateUser(username, password);

            String role = (user instanceof Trainee) ? "Trainee" : "Trainer";

            LOGGER.info("[{}] login success - 200 OK - username={}, role={}", txId, user.getUsername(), role);

            return ResponseEntity.ok("Login successful");
        } catch (Exception ex) {
            LOGGER.error("[{}] login failed - 400 BAD REQUEST - username={}", txId, username);
            throw ex;
        }
    }
}
