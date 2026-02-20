package org.example.policy;

import org.example.orchestrator.TraineeOrchestratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("component-test")
@Service
public class ComponentTestTraineeDeletionPolicy implements TraineeDeletionPolicy {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegretionTestTraineeDeletionPolicy.class);
    private final TraineeOrchestratorService traineeOrchestratorService;

    ComponentTestTraineeDeletionPolicy(TraineeOrchestratorService traineeOrchestratorService) {
        this.traineeOrchestratorService = traineeOrchestratorService;
    }

    @Override
    public void deleteTrainee(String username) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] Component TEST: Deleting trainer {}", txId, username);
        traineeOrchestratorService.deleteTraineeSaga(username);
    }
}
