package org.example.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import jakarta.servlet.*;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.UUID;

public class TransactionIdFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionIdFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String txId = UUID.randomUUID().toString();
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
