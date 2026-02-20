package org.workload.service;

import org.workload.dto.TrainerWorkloadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static org.workload.dto.ActionType.ADD;

@Service
public class ValidatorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorService.class);

    public boolean validateAddRequest(TrainerWorkloadRequest request) {
        String txId = MDC.get("transactionId");
        if (request.getTrainingDate().isBefore(LocalDate.now()) && request.getActionType().equals(ADD)) {
            LOGGER.warn("[{}] Invalid training parameters", txId);
            return false;
        }
        LOGGER.info("[{}] Trainer workload request was successfully validated", txId);
        return true;
    }
}
