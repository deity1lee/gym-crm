package org.example.filter;

import org.example.exception.UnauthorizedException;
import org.example.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public class AuthFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);

    private AuthService authService;

    @Override
    public void init(FilterConfig filterConfig) {
        WebApplicationContext ctx =
                WebApplicationContextUtils.getRequiredWebApplicationContext(
                        filterConfig.getServletContext()
                );

        this.authService = ctx.getBean(AuthService.class);
    }

    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/swagger-ui.html",
            "/swagger-resources",
            "/swagger-resources/configuration/ui",
            "/swagger-resources/configuration/security",
            "/v2/api-docs",
            "/webjars"
    );

    private boolean isExcluded(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (isExcluded(path)
                || ("POST".equals(request.getMethod()) &&
                "/trainees".equals(path))
                || ("POST".equals(request.getMethod()) &&
                "/trainers".equals(path))
                || ("GET".equals(request.getMethod()) &&
                "/auth".equals(path))) {
            chain.doFilter(req, res);
            return;
        }

        String username = request.getHeader("X-Auth-Username");
        String password = request.getHeader("X-Auth-Password");

        String txId = MDC.get("transactionId");

        try {
            if (username == null || username.isBlank()) {
                throw new UnauthorizedException("Missing or empty X-Auth-Username header");
            }
            if (password == null || password.isBlank()) {
                throw new UnauthorizedException("Missing or empty X-Auth-Password header");
            }
            LOGGER.debug("[{}] Starting authentication for user '{}' on path '{}'",
                    txId, username, path);
            authService.authenticateUser(username, password);
            chain.doFilter(req, res);

        } catch (UnauthorizedException e) {
            LOGGER.error("[{}] Authentication failed.", txId);
            HttpServletResponse response = (HttpServletResponse) res;
            response.reset();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain");
            response.getWriter().write("error: Unauthorized");
        }
    }

    @Override
    public void destroy() {

    }
}
