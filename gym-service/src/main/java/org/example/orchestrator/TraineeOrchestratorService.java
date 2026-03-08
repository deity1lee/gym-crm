package org.example.orchestrator;

import org.example.dto.ActionType;
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

import java.util.ArrayList;
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
        List<Training> trainings = trainee.getTrainings();
        List<Training> processedTrainings = new ArrayList<>();

        try {
            LOGGER.info("[{}] Trying to delete trainee and all trainings related to trainee", txId);
            for (Training training : trainings) {
                trainerWorkloadClient.changeWorkingHours(new TrainerWorkloadRequest(training, ActionType.DELETE));
                processedTrainings.add(training);
            }
            traineeService.deleteTrainee(username);
            LOGGER.info("[{}] Trainee {} deleted successfully", txId, username);
        } catch (Exception ex) {
            LOGGER.warn("[{}] Saga failed: Rolling back: trainee = {} and associated trainings were not deleted", txId, username);
            for (Training training : processedTrainings) {
                trainerWorkloadClient.changeWorkingHours(new TrainerWorkloadRequest(training, ActionType.ADD));
            }
            if (ex instanceof DownstreamServiceException) {
                throw new SagaFailedException("Failed to complete trainee deletion saga", ex);
            } else {
                throw ex;
            }
        }
    }
}
