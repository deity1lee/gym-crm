package org.example.service;

import org.example.actuator.prometheus.TrainerAssignmentMetrics;
import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.dao.TrainingTypeDao;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.policy.TraineeDeletionPolicy;
import org.example.utilities.TrainingSearchCriteria;
import org.hibernate.Hibernate;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class TraineeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingTypeDao trainingTypeDao;
    private AuthService authService;
    private ValidatorService validatorService;
    private TraineeDeletionPolicy deletionPolicy;

    private TrainerAssignmentMetrics metrics;

    @Autowired
    public void setTrainerAssignmentMetrics(TrainerAssignmentMetrics metrics) {
        this.metrics = metrics;
    }

    @Autowired
    public void setDeletionPolicy(TraineeDeletionPolicy deletionPolicy) {
        this.deletionPolicy = deletionPolicy;
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
    public void setTrainingTypeDao(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Autowired
    public void setValidatorService(ValidatorService validatorService) {
        this.validatorService = validatorService;
    }

    public Trainee createTrainee(Trainee trainee) {
        validatorService.validate(trainee);
        authService.assignCredentials(trainee);
        traineeDao.create(trainee);
        return trainee;
    }

    public Trainee selectTraineeByUsername(String usernameForSearch) {
        String txId = MDC.get("transactionId");
        Trainee trainee = traineeDao.findByUsername(usernameForSearch)
                .orElseThrow(() -> new IllegalArgumentException("Trainee with this username is not found."));
        LOGGER.info("[{}] Trainee by username {} is found",txId, usernameForSearch);
        Hibernate.initialize(trainee.getTrainers());
        Hibernate.initialize(trainee.getTrainers().stream().map(Trainer::getTrainingType).toList());
        return trainee;
    }

    public Trainee updateTrainee(Trainee newTrainee) {
        validatorService.validate(newTrainee);
        Trainee existing = selectTraineeByUsername(newTrainee.getUsername());
        existing.setFirstName(newTrainee.getFirstName());
        existing.setLastName(newTrainee.getLastName());
        existing.setAddress(newTrainee.getAddress());
        existing.setDateOfBirth(newTrainee.getDateOfBirth());
        existing.setActive(newTrainee.isActive());
        traineeDao.updateProfile(existing);

        Hibernate.initialize(existing.getTrainers());
        Hibernate.initialize(existing.getTrainers().stream().map(Trainer::getTrainingType).toList());
        return existing;
    }

    public void updateActivationStatus(String username, boolean isActive) {
        String txId = MDC.get("transactionId");
        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found"));
        LOGGER.info("[{}] Validation passed for changing active status", txId);
        trainee.setActive(isActive);
        LOGGER.warn("[{}] Changing active status {} for trainee {}", txId, isActive, username);
        traineeDao.updateProfile(trainee);
    }

    public void deleteTraineeByUsername(String username) {
        deletionPolicy.deleteTrainee(username);
    }

    public List<Training> getTraineeTrainings(String username, TrainingSearchCriteria criteria) {
        String txId = MDC.get("transactionId");

        if (criteria.getTraineeUsername() == null || criteria.getTraineeUsername().isBlank()) {
            LOGGER.warn("[{}] Missing traineeUsername in request", txId);
            throw new IllegalArgumentException("traineeUsername is required");
        }
        traineeDao.findByUsername(criteria.getTraineeUsername())
                .orElseThrow(() -> {
                    LOGGER.warn("[{}] Trainee '{}' not found in DB", txId, criteria.getTraineeUsername());
                    return new IllegalArgumentException(String.format("Trainee not found: %s", criteria.getTraineeUsername()));
                });

        if (criteria.getTrainerUsername() != null && !criteria.getTrainerUsername().isBlank()) {
            trainerDao.findByUsername(criteria.getTrainerUsername())
                    .orElseThrow(() -> {
                        LOGGER.warn("[{}] Trainer '{}' filter is invalid (not found)",
                                txId, criteria.getTrainerUsername());
                        return new IllegalArgumentException(String.format("Trainer not found: %s", criteria.getTrainerUsername()));
                    });
        }
        if (criteria.getFromDate() != null && criteria.getToDate() != null) {
            if (criteria.getFromDate().after(criteria.getToDate())) {
                LOGGER.warn("[{}] Invalid date range: fromDate={} after toDate={}",
                        txId, criteria.getFromDate(), criteria.getToDate());
                throw new IllegalArgumentException("fromDate must be before toDate");
            }
        }

        if (criteria.getTrainingTypeId() != null) {
            trainingTypeDao.findById(criteria.getTrainingTypeId())
                    .orElseThrow(() -> {
                        LOGGER.warn("[{}] Training type with id {} not found",
                                txId, criteria.getTrainingTypeId());
                        return new IllegalArgumentException(String.format("Training type with id %s not found", criteria.getTrainingTypeId()));
                    });
        }
        List<Training> trainings = traineeDao.findByTraineeUsernameAndCriteria(criteria);
        LOGGER.info("[{}] Training names for trainee {}:", txId, username);
        trainings.forEach(t ->
                LOGGER.info(" - {}", t.getName())
        );

        return trainings;
    }

    public List<Trainer> getUnassignedTrainers(String username) {
        String txId = MDC.get("transactionId");
        traineeDao.findByUsername(username)
                .orElseThrow(() -> {
                    LOGGER.warn("[{}] Trainee '{}' not found in DB", txId, username);
                    return new IllegalArgumentException(String.format("Trainee not found: %s", username));
                });

        LOGGER.debug("[{}] Trainee '{}' exists. Fetching unassigned trainers...", txId, username);

        List<Trainer> trainers = traineeDao.findUnassignedTrainers(username);

        LOGGER.info("[{}] Trainers (usernames) that are not assigned to trainee {}:", txId, username);
        trainers.forEach(t ->
                LOGGER.info(" - {}", t.getUsername())
        );

        return trainers;
    }

    public List<Trainer> updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        String txId = MDC.get("transactionId");

        return metrics.record(() -> {
            Trainee trainee = traineeDao.findByUsername(traineeUsername)
                    .orElseThrow(() -> new IllegalArgumentException("Trainee not found"));

            List<Trainer> newTrainers = new ArrayList<>();
            LOGGER.debug("[{}] Fetching trainers by username", txId);
            for (String trainerUsername : trainerUsernames) {
                Trainer trainer = trainerDao.findByUsername(trainerUsername)
                        .orElseThrow(() ->
                                new IllegalArgumentException(String.format("Trainer not found: %s", trainerUsername)));
                newTrainers.add(trainer);
            }
            trainee.setTrainers(newTrainers);
            traineeDao.updateProfile(trainee);
            return newTrainers;
        });
    }
}
