package org.example.service;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.utilities.TrainingSearchCriteria;
import org.hibernate.Hibernate;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.util.*;

@Transactional
@Service
public class TrainerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);

    private TrainerDao trainerDao;
    private TraineeDao traineeDao;
    private AuthService authService;
    private ValidatorService validatorService;

    @Autowired
    public void setValidatorService(ValidatorService validatorService) {
        this.validatorService = validatorService;
    }

    @Autowired
    public void setUserService(AuthService authService) {
        this.authService = authService;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    public Trainer createTrainer(Trainer trainer) {
        validatorService.validate(trainer);
        authService.assignCredentials(trainer);
        trainerDao.create(trainer);
        LOGGER.info("Created trainee profile for username: {}", trainer.getUsername());
        return trainer;
    }

    public Trainer selectTrainerByUsername(String usernameForSearch) {
        String txId = MDC.get("transactionId");
        LOGGER.debug("[{}] Fetching trainer with username '{}'", txId, usernameForSearch);

        Trainer trainer = trainerDao.findByUsername(usernameForSearch)
                .orElseThrow(() -> {
                    LOGGER.warn("[{}] Trainer '{}' not found", txId, usernameForSearch);
                    return new IllegalArgumentException("Trainer not found: " + usernameForSearch);
                });

        LOGGER.info("[{}] Trainer '{}' found successfully", txId, usernameForSearch);

        Hibernate.initialize(trainer.getTrainees());

        return trainer;
    }

    public Trainer updateTrainer(Trainer newTrainer) {
        validatorService.validate(newTrainer);
        validatorService.validateTrainingType(newTrainer.getTrainingType());
        Trainer existing = selectTrainerByUsername(newTrainer.getUsername());
        existing.setFirstName(newTrainer.getFirstName());
        existing.setLastName(newTrainer.getLastName());
        existing.setActive(newTrainer.isActive());
        existing.setTrainingType(newTrainer.getTrainingType());
        trainerDao.updateProfile(existing);
        Hibernate.initialize(existing.getTrainees());
        return existing;
    }

    public void updateActivationStatus(String username, boolean isActive) {
        String txId = MDC.get("transactionId");
        Trainer trainer = trainerDao.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));
        LOGGER.info("[{}] Validation passed for changing active status", txId);
        trainer.setActive(isActive);
        LOGGER.warn("[{}] Changing active status {} for trainer {}", txId, isActive, username);
        trainerDao.updateProfile(trainer);
    }

    public List<Training> getTrainerTrainings(TrainingSearchCriteria criteria) {

        String txId = MDC.get("transactionId");

        if (criteria.getTrainerUsername() == null || criteria.getTrainerUsername().isBlank()) {
            LOGGER.warn("[{}] Missing trainerUsername in request", txId);
            throw new IllegalArgumentException("trainerUsername is required");
        }
        Trainer trainer = trainerDao.findByUsername(criteria.getTrainerUsername())
                .orElseThrow(() -> {
                    LOGGER.warn("[{}] Trainer '{}' not found in DB", txId, criteria.getTrainerUsername());
                    return new IllegalArgumentException("Trainer not found: " + criteria.getTrainerUsername());
                });

        if (criteria.getTraineeUsername() != null && !criteria.getTraineeUsername().isBlank()) {
            traineeDao.findByUsername(criteria.getTraineeUsername())
                    .orElseThrow(() -> {
                        LOGGER.warn("[{}] Trainee '{}' filter is invalid (not found)",
                                txId, criteria.getTraineeUsername());
                        return new IllegalArgumentException("Trainee not found: " + criteria.getTraineeUsername());
                    });
        }
        if (criteria.getFromDate() != null && criteria.getToDate() != null) {
            if (criteria.getFromDate().after(criteria.getToDate())) {
                LOGGER.warn("[{}] Invalid date range: fromDate={} after toDate={}",
                        txId, criteria.getFromDate(), criteria.getToDate());
                throw new IllegalArgumentException("fromDate must be before toDate");
            }
        }
        List<Training> trainings = trainerDao.findByTrainerUsernameAndCriteria(criteria);
        LOGGER.info("[{}] Training names for trainer {}:", txId, criteria.getTrainerUsername());
        trainings.forEach(t ->
                LOGGER.info(" - {}", t.getName()));

        return trainings;
    }
}
