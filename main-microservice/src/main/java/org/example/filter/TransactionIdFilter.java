package org.example.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import jakarta.servlet.*;
import java.io.IOException;
import java.util.UUID;

public class TransactionIdFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionIdFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        String txId = UUID.randomUUID().toString();
        MDC.put("transactionId", txId);

        LOGGER.info("[{}] Incoming request", txId);

        try {
            chain.doFilter(req, res);
        } finally {
            LOGGER.info("[{}] Finished request", txId);
            MDC.clear();
        }
    }
}
