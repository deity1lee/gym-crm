package org.example.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.example.exception.BruteForceAttemptException;
import org.example.service.AuthService;
import org.example.service.LoginAttemptService;
import org.example.service.TokenBlacklistService;
import org.example.utilities.JwtUtil;
import org.example.utilities.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;

    private final Counter successfulLoginCounter;

    private final AuthService authService;

    private final LoginAttemptService loginAttemptService;

    private final JwtUtil jwtUtil;

    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(TokenBlacklistService tokenBlacklistService, JwtUtil jwtUtil,
                          LoginAttemptService loginAttemptService, AuthService authService,
                          AuthenticationManager authenticationManager, MeterRegistry meterRegistry) {
        this.tokenBlacklistService = tokenBlacklistService;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
        this.authService = authService;
        this.authenticationManager = authenticationManager;
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
            @ApiResponse(responseCode = "423", description = "Too many login attempts"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestHeader("X-Auth-Username") String username,
                                        @RequestHeader("X-Auth-Password") String password) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] GET /auth/login - login(username={})", txId, username);
        try {
            authService.findByUsername(username).ifPresent(loginAttemptService::unlockIfNeeded);

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            LOGGER.info("[{}] login success - 200 OK - username={}", txId, username);
            successfulLoginCounter.increment();
            authService.findByUsername(username).ifPresent(user -> {
                user.setFailedAttempts(0);
                user.setLockTime(null);
                user.setAccountNonLocked(true);
                authService.updateUser(user);
            });
            String token = jwtUtil.generateToken(username);
            return ResponseEntity.ok(token);
        }
        catch (BruteForceAttemptException ex) {
            throw ex;
        }
        catch (Exception ex) {
            authService.findByUsername(username).ifPresent(user -> {
                int attempts = user.getFailedAttempts() + 1;
                user.setFailedAttempts(attempts);

                if (attempts >= SecurityConstants.MAX_FAILED_ATTEMPTS) {
                    user.setAccountNonLocked(false);
                    LOGGER.error("[{}] account is blocked - username={}", txId, username);
                    user.setLockTime(LocalDateTime.now());
                }
                authService.updateUser(user);
            });
            LOGGER.error("[{}] login failed - 400 BAD REQUEST - username={}", txId, username);
            throw ex;
        }
    }

    @Operation(summary = "Logout user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logout successful"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            tokenBlacklistService.blacklist(header.substring(7));
        }
        return ResponseEntity.noContent().build();
    }
}
