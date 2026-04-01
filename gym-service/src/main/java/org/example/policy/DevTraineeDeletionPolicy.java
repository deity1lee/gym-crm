package org.example.policy;

import jakarta.transaction.Transactional;
import org.example.dao.TraineeDao;
import org.example.entities.Trainee;
import org.example.orchestrator.TraineeOrchestratorService;
import org.example.orchestrator.TrainingOrchestratorService;
import org.example.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile({"dev", "docker"})
@Transactional
@Service
public class DevTraineeDeletionPolicy implements TraineeDeletionPolicy{

    private static final Logger LOGGER = LoggerFactory.getLogger(DevTraineeDeletionPolicy.class);
    private final TraineeOrchestratorService traineeOrchestratorService;

    DevTraineeDeletionPolicy(TraineeOrchestratorService traineeOrchestratorService) {
        this.traineeOrchestratorService = traineeOrchestratorService;
    }

    @Override
    public void deleteTrainee(String username) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] DEV: Deleting trainer {}", txId, username);
        traineeOrchestratorService.deleteTraineeSaga(username);
    }
}
