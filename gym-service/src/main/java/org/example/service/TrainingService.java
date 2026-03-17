package org.example.service;

import org.example.dao.*;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class TrainingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingService.class);

    private TrainingDao trainingDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingTypeDao trainingTypeDao;

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

    public Training addTraining(String traineeUsername,
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

        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> {
                    LOGGER.warn("[{}] Trainee '{}' not found in DB", txId, traineeUsername);
                    return new IllegalArgumentException(String.format("Trainee not found: %s", traineeUsername));
                });

        if (trainerUsername == null || trainerUsername.isBlank()) {
            LOGGER.warn("[{}] trainerUsername is missing in request", txId);
            throw new IllegalArgumentException("trainerUsername is required");
        }

        Trainer trainer = trainerDao.findByUsername(trainerUsername)
                .orElseThrow(() -> {
                    LOGGER.warn("[{}] Trainer '{}' not found in DB", txId, trainerUsername);
                    return new IllegalArgumentException(String.format("Trainer not found: %s", trainerUsername));
                });

        if (trainingDate == null) {
            LOGGER.warn("[{}] trainingDate is missing in request", txId);
            throw new IllegalArgumentException("trainingDate is required");
        }

        if (duration == null || duration <= 0 || duration > 8) {
            LOGGER.warn("[{}] Invalid trainingDuration: {}", txId, duration);
            throw new IllegalArgumentException("trainingDuration must be positive");
        }

        if (trainingDao.findTrainingByDateAndUsers(trainee.getId(), trainer.getId(), trainingDate).isPresent()) {
            LOGGER.warn("[{}] Specified trainee and trainer already have planned session on {}", txId, trainingDate);
            throw new IllegalArgumentException("Specified trainee and trainer already have planned session on this day");
        }

        LOGGER.info("[{}] Validation for addTraining() passed successfully", txId);

        Training training = new Training(
                trainee,
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
        return training;
    }

    public void deleteTraining(Long id) {
        Training training = findTrainingById(id);
        trainingDao.deleteTraining(training);
    }

    public TrainingType findTrainingTypeById(Long id) {
        return trainingTypeDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid training type id"));
    }

    public Training findTrainingById(Long id) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] Searching training by id", txId);

        return trainingDao.findTrainingById(id);
    }
}
