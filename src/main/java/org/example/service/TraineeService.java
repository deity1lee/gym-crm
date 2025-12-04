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

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class TraineeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private AuthService authService;
    private ValidatorService validatorService;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setAuthService(AuthService authService) {
            this.authService = authService;
    }

    @Autowired
    public void setValidatorService(ValidatorService validatorService) {
        this.validatorService = validatorService;
    }

    public void createTrainee(Trainee trainee) {
        validatorService.validate(trainee);
        assignCredentials(trainee);
        traineeDao.create(trainee);
        LOGGER.info("Created trainee profile for username: {}", trainee.getUsername());
    }

    public Trainee selectTraineeByUsername(String username, String password, String traineeUsernameForSearch) {
        authService.authenticateUser(username, password);
        Trainee trainee = traineeDao.findByUsername(traineeUsernameForSearch)
                .orElseThrow(() -> new IllegalArgumentException("Trainee by this username is not found."));
        LOGGER.info("Trainee by username {} is found", traineeUsernameForSearch);
        return trainee;
    }

    public void changePassword(String username,String password,String traineeUsername, String oldPassword, String newPassword) {
        authService.authenticateUser(username, password);
        Trainee trainee = selectTraineeByUsername(traineeUsername, oldPassword, traineeUsername);
        if (!validatorService.isStrongPassword(newPassword)) {
            LOGGER.info("Password was not updated. Provided password is not strong");
            return;
        }
        trainee.setPassword(newPassword);
        traineeDao.updatePassword(trainee.getId(), newPassword);
        LOGGER.info("Password was changed successfully!");
    }

    public void updateTrainee(String username, String password, Trainee newTrainee) {
        validatorService.validate(newTrainee);
        Trainee existing = selectTraineeByUsername(username, password, newTrainee.getUsername());
        existing.setFirstName(newTrainee.getFirstName());
        existing.setLastName(newTrainee.getLastName());
        existing.setAddress(newTrainee.getAddress());
        traineeDao.updateProfile(newTrainee);
    }

    public void activateTrainee(String username, String password, String traineeUsername) {
        Trainee trainee = selectTraineeByUsername(username, password, traineeUsername);
        traineeDao.activateTrainee(trainee);
    }

    public void deActivateTrainee(String username, String password, String traineeUsername) {
        Trainee trainee = selectTraineeByUsername(username, password, traineeUsername);
        traineeDao.deactivateTrainee(trainee);
    }

    public void deleteTraineeByUsername(String username, String password, String traineeUsername) {
        Trainee trainee = selectTraineeByUsername(username, password, traineeUsername);
        traineeDao.deleteTrainee(trainee);
    }

    public List<Training> getTraineeTrainings(String username, String password, String traineeUsername, TrainingSearchCriteria criteria) {
        selectTraineeByUsername(username, password, traineeUsername);
        List<Training> trainings = traineeDao.findByTraineeUsernameAndCriteria(traineeUsername, criteria);
        LOGGER.info("Training names for trainee {}:", username);
        trainings.forEach(t ->
                LOGGER.info(" - {}", t.getName())
        );
        return trainings;
    }

    public List<Trainer> getUnassignedTrainers(String username, String password, String traineeUsername) {
        selectTraineeByUsername(username, password, traineeUsername);
        List<Trainer> trainers = traineeDao.findUnassignedTrainers(traineeUsername);
        LOGGER.info("Trainers (usernames) that are not assigned to trainee {}:", traineeUsername);
        trainers.forEach(t ->
                LOGGER.info(" - {}", t.getUsername())
        );
        return trainers;
    }

    public void updateTraineeTrainers(String username, String password, String traineeUsername, List<Trainer> newTrainers) {
        Trainee trainee = selectTraineeByUsername(username, password, traineeUsername);
        List<Trainer> newTrainerList = newTrainers.stream()
                .map(trainer -> trainerDao.findByUsername(trainer.getUsername())
                        .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + trainer.getUsername())))
                .toList();
        trainee.setTrainers(new ArrayList<>(newTrainerList));
        traineeDao.updateProfile(trainee);
        LOGGER.info("Updated trainers for trainee {}", traineeUsername);
    }

    private void assignCredentials(Trainee trainee) {
        long traineeCount = traineeDao.countByFullName(trainee.getFirstName(), trainee.getLastName());
        long trainerCount = trainerDao.countByFullName(trainee.getFirstName(), trainee.getLastName());
        long total = traineeCount + trainerCount;

        String username = CredentialGenerator.generateUsername(
                trainee.getFirstName(),
                trainee.getLastName(),
                total
        );

        String password = CredentialGenerator.generateRandomPassword();

        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setActive(true);

        LOGGER.debug("Assigned credentials for {}: username={}",
                trainee.getFirstName(), username);
    }
}
