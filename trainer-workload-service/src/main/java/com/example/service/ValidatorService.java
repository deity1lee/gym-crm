package com.example.service;

import com.example.dto.TrainerWorkloadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ValidatorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorService.class);

    public void validateTrainerWorkloadRequest(TrainerWorkloadRequest request) {
        String txId = MDC.get("transactionId");
        if (request.getTrainingDate().isBefore(LocalDate.now())) {
            LOGGER.warn("[{}] Invalid training parameters", txId);
            throw new IllegalArgumentException("Training has already taken place");
        }
        LOGGER.info("[{}] Trainer workload request was successfully validated", txId);
    }
}
