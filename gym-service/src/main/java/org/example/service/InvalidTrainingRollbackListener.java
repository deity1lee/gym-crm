package org.example.service;

import jakarta.transaction.Transactional;
import org.example.dao.TrainingDao;
import org.example.dto.ActionType;
import org.example.dto.TrainerWorkloadRequest;
import org.example.entities.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import static org.example.dto.ActionType.ADD;

@Component
public class InvalidTrainingRollbackListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvalidTrainingRollbackListener.class);
    private final TrainingDao trainingDao;

    @Value("${security.api-key}")
    private String apiKey;

    public InvalidTrainingRollbackListener(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @JmsListener(destination = "invalid.trainer.workload")
    @Transactional
    public void rollbackTrainings(@Payload TrainerWorkloadRequest request, @Header(name = "X-Transaction-Id") String txId,
                                  @Header(name = "X-API-KEY") String requestKey) {
        MDC.put("transactionId", txId);
        if (apiKey != null && apiKey.equals(requestKey)) {
            try {
                Training training = trainingDao.findTrainingByTrainerWorkloadRequest(request);
                if (request.getActionType().equals(ADD)) {
                    trainingDao.deleteTraining(training);
                }
                LOGGER.warn("[{}] Unsuccessful trainer workload change: Training was compensated", txId);
            } catch (IllegalArgumentException e) {
                LOGGER.warn("[{}] Unsuccessful trainer workload change: Training was not found", txId);
                throw e;
            }
        } else {
            LOGGER.info("[{}] Request is not authenticated: {}", txId, request.getTrainerUsername());
            throw new BadCredentialsException("Invalid API key");
        }
    }
}
