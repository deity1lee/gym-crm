package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dao.*;
import org.example.dto.ActionType;
import org.example.dto.TrainerWorkloadRequest;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.example.entities.WorkloadOutbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class TrainingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingService.class);

    private TrainerService trainerService;
    private TraineeService traineeService;
    private TrainingDao trainingDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingTypeDao trainingTypeDao;
    private OutboxDao outboxDao;

    @Autowired
    public void setTrainerService(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @Autowired
    public void setTraineeService(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @Autowired
    public void setTrainingTypeDao(TrainingTypeDao trainingTypeDao){
        this.trainingTypeDao = trainingTypeDao;
    }

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setOutboxDao(OutboxDao outboxDao) {
        this.outboxDao = outboxDao;
    }

    public void addTraining(String traineeUsername,
                            String trainerUsername,
                            String trainingName,
                            TrainingType trainingType,
                            LocalDate trainingDate,
                            Long duration) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] Persisting training", txId);
        if (traineeUsername == null || traineeUsername.isBlank()) {
            LOGGER.warn("[{}] traineeUsername is missing in request", txId);
            throw new IllegalArgumentException("traineeUsername is required");
        }

        traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> {
                    LOGGER.warn("[{}] Trainee '{}' not found in DB", txId, traineeUsername);
                    return new IllegalArgumentException(String.format("Trainee not found: %s", traineeUsername));
                });

        if (trainerUsername == null || trainerUsername.isBlank()) {
            LOGGER.warn("[{}] trainerUsername is missing in request", txId);
            throw new IllegalArgumentException("trainerUsername is required");
        }

        trainerDao.findByUsername(trainerUsername)
                .orElseThrow(() -> {
                    LOGGER.warn("[{}] Trainer '{}' not found in DB", txId, trainerUsername);
                    return new IllegalArgumentException(String.format("Trainer not found: %s", trainerUsername));
                });

        if (trainingDate == null) {
            LOGGER.warn("[{}] trainingDate is missing in request", txId);
            throw new IllegalArgumentException("trainingDate is required");
        }

        if (duration == null || duration <= 0) {
            LOGGER.warn("[{}] Invalid trainingDuration: {}", txId, duration);
            throw new IllegalArgumentException("trainingDuration must be positive");
        }

        LOGGER.info("[{}] Validation for addTraining() passed successfully", txId);

        Trainer trainer = trainerService.selectTrainerByUsername(trainerUsername);
        Training training = new Training(
                traineeService.selectTraineeByUsername(traineeUsername),
                trainer,
                trainingName,
                trainingType,
                trainingDate,
                duration
        );
        trainingDao.addTraining(training);
        LOGGER.info(
                "[{}] Training persisted id={} trainer={}",
                txId,
                training.getId(),
                training.getTrainer().getUsername()
        );
        LOGGER.info("[{}] Notifying workload service", txId);

        TrainerWorkloadRequest request = new TrainerWorkloadRequest(training, ActionType.ADD);

       outboxDao.createPendingRequest(request);
    }

    public void deleteTraining(Long id) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] Trying to delete training by id", txId);

        Training training = findTrainingById(id);

        TrainerWorkloadRequest request = new TrainerWorkloadRequest(training, ActionType.DELETE);

        trainingDao.deleteTraining(training);

        outboxDao.createPendingRequest(request);
        LOGGER.info("[{}] Request for decreasing trainer workload was created", txId);
    }

    public TrainingType findTrainingTypeById(Long id) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] Searching training type by id", txId);

        return trainingTypeDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid training type id"));
    }

    public Training findTrainingById(Long id) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] Searching training type by id", txId);

        return trainingDao.findTrainingById(id);
    }
}
