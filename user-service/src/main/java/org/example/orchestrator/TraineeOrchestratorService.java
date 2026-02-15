package org.example.orchestrator;

import org.example.dto.ActionType;
import org.example.dto.TraineeSnapshot;
import org.example.dto.TrainerWorkloadRequest;
import org.example.entities.Trainee;
import org.example.entities.Training;
import org.example.exception.DownstreamServiceException;
import org.example.exception.SagaFailedException;
import org.example.service.TraineeService;
import org.example.service.TrainerWorkloadClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeOrchestratorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeOrchestratorService.class);

    private TraineeService traineeService;
    private TrainerWorkloadClient trainerWorkloadClient;

    @Autowired
    public void setTraineeService(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @Autowired
    public void setTrainerWorkloadClient(TrainerWorkloadClient trainerWorkloadClient) {
        this.trainerWorkloadClient = trainerWorkloadClient;
    }

    public void deleteTraineeSaga(String username) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] LOCAL: Deleting trainee {}", txId, username);
        Trainee trainee = traineeService.selectTraineeByUsername(username);
        TraineeSnapshot snapshot = TraineeSnapshot.from(trainee);
        List<Training> processedTrainings = trainee.getTrainings();

        try {
            LOGGER.info("[{}] Trying to delete trainee and all trainings related to trainee", txId);
            traineeService.deleteTrainee(username);
            for (Training training : processedTrainings) {
                TrainerWorkloadRequest request = new TrainerWorkloadRequest(training, ActionType.DELETE);
                trainerWorkloadClient.changeWorkingHours(request);
            }
            LOGGER.info("[{}] Trainee {} deleted successfully", txId, username);
        } catch (Exception ex) {
            if (ex instanceof DownstreamServiceException) {
                LOGGER.error("[{}] Saga failed: Rolling back: Recreating deleted trainee = {} and associated trainings", txId, username);
                traineeService.recreate(snapshot);
                throw new SagaFailedException("Failed to complete trainee deletion saga", ex);
            } else {
                LOGGER.warn("[{}] Trainee was not deleted, according trainings and trainer's workload was not changed", txId);
                throw ex;
            }
        }
    }
}
