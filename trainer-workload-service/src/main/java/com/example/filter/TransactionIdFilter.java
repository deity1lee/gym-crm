package com.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import jakarta.servlet.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class TransactionIdFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionIdFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String txId = Optional.ofNullable(request.getHeader("X-Transaction-Id")).orElse(UUID.randomUUID().toString());
        MDC.put("transactionId", txId);

        LOGGER.info("[{}] Incoming request", txId);

        try {
            LOGGER.info("[{}] {} {}", txId, request.getMethod(), request.getRequestURI());
            chain.doFilter(req, res);
        } finally {
            HttpStatus status = HttpStatus.valueOf(response.getStatus());
            LOGGER.info("[{}] {} {}", txId, status, status.getReasonPhrase());
            LOGGER.info("[{}] Finished request", txId);
            MDC.clear();
        }
    }
}
