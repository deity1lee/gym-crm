package org.example.service;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.User;
import org.example.utilities.CredentialGenerator;
import org.example.utilities.TrainingSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Service
public class TrainerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);

    private TrainerDao trainerDao;
    private TraineeDao traineeDao;
    private AuthService authService;
    private ValidatorService validatorService;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Autowired
    public void setValidatorService(ValidatorService validatorService) {
        this.validatorService = validatorService;
    }

    public void createTrainer(Trainer trainer) {
        validatorService.validateFirstName(trainer.getFirstName());
        validatorService.validateLastName(trainer.getLastName());
        assignCredentials(trainer);
        trainerDao.create(trainer);
        LOGGER.info("Created trainee profile for username: {}", trainer.getUsername());
    }

    public Trainer selectByUsername(String username, String password, String usernameForSearch) {
        authService.authenticateTrainer(username, password)
                .orElseThrow(() -> new IllegalArgumentException("Not authenticated trainer for doing select."));
        Trainer trainer = trainerDao.findByUsername(usernameForSearch)
                .orElseThrow(() -> new IllegalArgumentException("Trainer by this username is not found."));
        LOGGER.info("Trainer by username {} is found", usernameForSearch);
        return trainer;
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainer trainer = authService.authenticateTrainer(username, oldPassword)
                .orElseThrow(() -> new IllegalArgumentException("Not authenticated trainer for password update"));
        if (!validatorService.isStrongPassword(newPassword)) {
            LOGGER.info("Password was not updated. Provided password is not strong");
            return;
        }
        trainer.setPassword(newPassword);
        trainerDao.updatePassword(trainer.getId(), newPassword);
        LOGGER.info("Password was changed successfully!");
    }

    public void updateTrainer(String username, String password, Trainer newTrainer) {
        User existing = authService.authenticateTrainer(username,password)
                .orElseThrow(() -> new IllegalArgumentException("Not authenticated trainer for updating profile."));
        validatorService.validateFirstName(newTrainer.getFirstName());
        existing.setFirstName(newTrainer.getFirstName());
        validatorService.validateLastName(newTrainer.getLastName());
        existing.setLastName(newTrainer.getLastName());
        trainerDao.updateProfile((Trainer) existing);
    }

    public void activateTrainer(String username, String password) {
        Trainer trainer1 = authService.authenticateTrainer(username, password)
                .orElseThrow(() -> new IllegalArgumentException("Not authenticated trainer for doing status change"));
        trainerDao.activateTrainer(trainer1);
    }

    public void deActivateTrainer(String username, String password) {
        Trainer trainer1 = authService.authenticateTrainer(username, password)
                .orElseThrow(() -> new IllegalArgumentException("Not authenticated trainer for doing status change"));
        trainerDao.deActivateTrainer(trainer1);
    }

    public List<Training> getTrainerTrainings(String username, String password, TrainingSearchCriteria criteria) {
        authService.authenticateTrainer(username, password)
                .orElseThrow(() -> new IllegalArgumentException("Not authenticated trainer for searching trainings."));
        List<Training> trainings = trainerDao.findByTrainerUsernameAndCriteria(username, criteria);
        LOGGER.info("Training names for trainer {}:", username);
        trainings.forEach(t ->
                LOGGER.info(" - {}", t.getName())
        );
        return trainings;
    }

    private void assignCredentials(Trainer trainer) {
        long traineeCount = traineeDao.countByFullName(trainer.getFirstName(), trainer.getLastName());
        long trainerCount = trainerDao.countByFullName(trainer.getFirstName(), trainer.getLastName());
        long total = traineeCount + trainerCount;

        String username = CredentialGenerator.generateUsername(
                trainer.getFirstName(),
                trainer.getLastName(),
                total
        );

        String password = CredentialGenerator.generateRandomPassword();

        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setActive(true);

        LOGGER.debug("Assigned credentials for {}: username={}, password={}",
                trainer.getFirstName(), username, password);
    }
}

