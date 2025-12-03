package org.example.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionIdFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionIdFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        String txId = UUID.randomUUID().toString();
        MDC.put("transactionId", txId);

        LOGGER.info("[{}] Incoming request",txId);

        try {
            chain.doFilter(req, res);
        } finally {
            LOGGER.info("[{}] Finished request", txId);
            MDC.clear();
        }
    }

    @Override
    public void destroy() {}
}


