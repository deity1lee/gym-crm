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
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TransactionIdFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionIdFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String txId = Optional.ofNullable(request.getHeader("X-Transaction-Id")).orElse(UUID.randomUUID().toString());
        MDC.put("transactionId", txId);

        LOGGER.info("[{}] Incoming request", txId);

        try {
            LOGGER.info("[{}] {} {}", txId, request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
        } finally {
            HttpStatus status = HttpStatus.valueOf(response.getStatus());
            LOGGER.info("[{}] {} {}", txId, status, status.getReasonPhrase());
            LOGGER.info("[{}] Finished request", txId);
            MDC.clear();
        }
    }
}
