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
            "/webjars",
            "/auth"
    );

    private boolean isExcluded(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (isExcluded(path)) {
            chain.doFilter(req, res);
            return;
        }

        String username = request.getHeader("X-Auth-Username");
        String password = request.getHeader("X-Auth-Password");

        String txId = MDC.get("transactionId");

        LOGGER.debug("[{}] Starting authentication for user '{}' on path '{}'",
                txId, username, path);

        try {
            authService.authenticateUser(username, password);
            LOGGER.info("[{}] Authentication succeeded for user '{}'", txId, username);
        } catch (UnauthorizedException e) {
            LOGGER.warn("[{}] Authentication failed for user '{}': {}", txId, username, e.getMessage());
            throw e;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {

    }
}
