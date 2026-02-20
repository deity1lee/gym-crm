package org.example.exception;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Component
public class JmsErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsErrorHandler.class);

    @Override
    public void handleError(Throwable t) {
        Throwable root = NestedExceptionUtils.getRootCause(t);
        LOGGER.error("[{}] An error occurred in the JMS listener: {}", MDC.get("transactionId"), root.getMessage());
        throw new RuntimeException(root);
    }
}