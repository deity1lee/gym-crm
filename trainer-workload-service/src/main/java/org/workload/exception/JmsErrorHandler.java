package org.workload.exception;

import org.slf4j.MDC;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JmsErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsErrorHandler.class);

    @Override
    public void handleError(Throwable t) {
        Throwable root = NestedExceptionUtils.getRootCause(t);
        String txId = MDC.get("transactionId");
        if (root instanceof IllegalArgumentException) {
            LOGGER.warn("[{}] Discarding invalid message: {}", txId, root.getMessage());
        }
        if (root instanceof WorkloadUpdateRequestAuthException) {
            LOGGER.warn("[{}] Message was not processed due to bad credentials", txId);
        }
    }
}
