package org.example.policy;

import org.example.entities.Trainee;
import org.example.dao.TraineeDao;
import org.example.orchestrator.TraineeOrchestratorService;
import org.example.orchestrator.TrainingOrchestratorService;
import org.example.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile({"stg", "docker"})
@Service
public class StgTraineeDeletionPolicy implements TraineeDeletionPolicy {

    private static final Logger LOGGER = LoggerFactory.getLogger(StgTraineeDeletionPolicy.class);
    private final TraineeOrchestratorService traineeOrchestratorService;

    StgTraineeDeletionPolicy(TraineeOrchestratorService traineeOrchestratorService) {
        this.traineeOrchestratorService = traineeOrchestratorService;
    }

    @Override
    public void deleteTrainee(String username) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] STG: Deleting trainer {}", txId, username);
        traineeOrchestratorService.deleteTraineeSaga(username);
    }
}
