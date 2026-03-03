package org.example.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import jakarta.servlet.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionIdFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionIdFilter.class);

    @Override
    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {


        String txId = UUID.randomUUID().toString();
        MDC.put("transactionId", txId);


        LOGGER.info("[{}] Incoming request", txId);

        try {
            LOGGER.info("[{}] {} {}", txId, req.getMethod(), req.getRequestURI());
            chain.doFilter(req, res);
        } finally {
            HttpStatus status = HttpStatus.valueOf(res.getStatus());
            LOGGER.info("[{}] {} {}", txId, status, status.getReasonPhrase());
            LOGGER.info("[{}] Finished request", txId);
            MDC.clear();
        }
    }
}
