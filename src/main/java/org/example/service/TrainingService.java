package org.example.service;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.dao.TrainingDao;
import org.example.dao.TrainingTypeDao;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

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

    public void addTraining(String traineeUsername,
                            String trainerUsername,
                            String trainingName,
                            TrainingType trainingType,
                            Date trainingDate,
                            Long duration) {

        String txId = MDC.get("transactionId");

        if (traineeUsername == null || traineeUsername.isBlank()) {
            LOGGER.warn("[{}] traineeUsername is missing in request", txId);
            throw new IllegalArgumentException("traineeUsername is required");
        }

        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> {
                    LOGGER.warn("[{}] Trainee '{}' not found in DB", txId, traineeUsername);
                    return new IllegalArgumentException("Trainee not found: " + traineeUsername);
                });

        if (trainerUsername == null || trainerUsername.isBlank()) {
            LOGGER.warn("[{}] trainerUsername is missing in request", txId);
            throw new IllegalArgumentException("trainerUsername is required");
        }

        Trainer trainer = trainerDao.findByUsername(trainerUsername)
                .orElseThrow(() -> {
                    LOGGER.warn("[{}] Trainer '{}' not found in DB", txId, trainerUsername);
                    return new IllegalArgumentException("Trainer not found: " + trainerUsername);
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

        Training training = new Training(
                traineeService.selectTraineeByUsername(traineeUsername),
                trainerService.selectTrainerByUsername(trainerUsername),
                trainingName,
                trainingType,
                trainingDate,
                duration
        );
        trainingDao.addTraining(training);
    }

    public TrainingType findTrainingTypeById(Long id) {
        return trainingTypeDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid training type id"));
    }
}
