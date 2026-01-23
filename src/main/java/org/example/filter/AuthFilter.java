package org.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);

    private final AuthService authService;

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String txId = MDC.get("transactionId");
        String path = request.getRequestURI();

        if (isPublicEndpoint(path)
                || ("POST".equals(request.getMethod()) &&
                "/trainees".equals(path))
                || ("POST".equals(request.getMethod()) &&
                "/trainers".equals(path))
                || ("GET".equals(request.getMethod()) &&
                "/auth".equals(path))) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = request.getHeader("X-Auth-Username");
        String password = request.getHeader("X-Auth-Password");

        if (username == null || password == null ||
                username.isBlank() || password.isBlank()) {

            LOGGER.warn("[{}] Missing authentication headers", txId);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing credentials");
            return;
        }

        try {
            authService.authenticateUser(username, password);
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            LOGGER.warn("[{}] Authentication failed for user '{}'", txId, username);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
        }
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator");
    }
}
