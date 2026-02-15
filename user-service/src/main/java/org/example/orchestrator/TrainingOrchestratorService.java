package org.example.orchestrator;

import org.example.dto.ActionType;
import org.example.dto.TrainerWorkloadRequest;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.example.exception.DownstreamServiceException;
import org.example.exception.SagaFailedException;
import org.example.service.TrainerWorkloadClient;
import org.example.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TrainingOrchestratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingOrchestratorService.class);

    private TrainingService trainingService;
    private TrainerWorkloadClient trainerWorkloadClient;

    @Autowired
    public void setTrainingService(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @Autowired
    public void setTrainerWorkloadClient(TrainerWorkloadClient trainerWorkloadClient) {
        this.trainerWorkloadClient = trainerWorkloadClient;
    }

    public void addTraining(String traineeUsername, String trainerUsername, String trainingName, TrainingType trainingTypeById, LocalDate trainingDate, Long trainingDuration) {
        String txId = MDC.get("transactionId");
        Training training = null;
        try {
            training = trainingService.addTraining(traineeUsername, trainerUsername, trainingName, trainingTypeById, trainingDate, trainingDuration);
            TrainerWorkloadRequest request = new TrainerWorkloadRequest(training, ActionType.ADD);
            trainerWorkloadClient.changeWorkingHours(request);
            LOGGER.info("[{}] Training with id={} was created", txId, training.getId());
        } catch (Exception ex){
            if (ex instanceof DownstreamServiceException) {
                LOGGER.warn("[{}] Failed to add working hours of training's trainer with training id={}, deleting created training", txId, training.getId());
                trainingService.deleteTraining(training.getId());
            } else {
                LOGGER.warn("[{}] Training was not created, according trainer's workload was not changed", txId);
            }
            throw new SagaFailedException("Failed to create the training", ex);
        }
    }

    public void deleteTraining(Long id) {
        String txId = MDC.get("transactionId");
        Training training = null;
        try {
            training = trainingService.findTrainingById(id);
            trainingService.deleteTraining(id);
            TrainerWorkloadRequest request = new TrainerWorkloadRequest(training, ActionType.DELETE);
            trainerWorkloadClient.changeWorkingHours(request);
            LOGGER.info("[{}] Training with id={} was deleted", txId, training.getId());
        } catch (Exception ex){
            if (ex instanceof DownstreamServiceException) {
                LOGGER.warn("[{}] Failed to decrease working hours of training's trainer with training id={}, recreating deleted training", txId, training.getId());
                trainingService.addTraining(
                        training.getTrainee().getUsername(),
                        training.getTrainer().getUsername(),
                        training.getName(),
                        training.getTrainingType(),
                        training.getDate(),
                        training.getDuration());
                throw new SagaFailedException("Failed to delete the training", ex);
            } else {
                LOGGER.warn("[{}] Training was not deleted, according trainer's workload was not changed", txId);
                throw ex;
            }
        }
    }
}
