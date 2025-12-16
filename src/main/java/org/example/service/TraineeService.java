package org.example.service;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
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
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Trainee with username '%s' was not found", traineeUsernameForSearch)
                ));

        LOGGER.info(String.format(
                "Trainee with username '%s' was found",
                traineeUsernameForSearch
        ));

        return trainee;
    }

    public void changePassword(String username, String password, String traineeUsername,
                               String oldPassword, String newPassword) {

        authService.authenticateUser(username, password);
        Trainee trainee = selectTraineeByUsername(traineeUsername, oldPassword, traineeUsername);

        if (!validatorService.isStrongPassword(newPassword)) {
            LOGGER.info("Password was not updated. Provided password is not strong");
            return;
        }

        trainee.setPassword(newPassword);
        traineeDao.updatePassword(trainee.getId(), newPassword);

        LOGGER.info("Password was changed successfully");
    }

    public void updateTrainee(String username, String password, Trainee newTrainee) {
        validatorService.validate(newTrainee);

        authService.authenticateUser(username, password);
        Trainee existing = traineeDao.findByUsername(newTrainee.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Trainee with username '%s' was not found", newTrainee.getUsername())
                ));
        existing.setFirstName(newTrainee.getFirstName());
        existing.setLastName(newTrainee.getLastName());
        existing.setAddress(newTrainee.getAddress());

        traineeDao.updateProfile(existing);

        LOGGER.info("Updated trainee profile for username: {}", existing.getUsername());
    }

    public void activateTrainee(String username, String password, String traineeUsername) {
        authService.authenticateUser(username, password);
        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Trainee with username '%s' was not found", traineeUsername)
                ));        traineeDao.activateTrainee(trainee);

        LOGGER.info(String.format(
                "Activated trainee with username: %s",
                traineeUsername
        ));
    }

    public void deActivateTrainee(String username, String password, String traineeUsername) {
        authService.authenticateUser(username, password);
        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Trainee with username '%s' was not found", traineeUsername)
                ));        traineeDao.deactivateTrainee(trainee);

        LOGGER.info("Deactivated trainee with username: {}", traineeUsername);
    }

    public void deleteTraineeByUsername(String username, String password, String traineeUsername) {
        authService.authenticateUser(username, password);
        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Trainee with username '%s' was not found", traineeUsername)
                ));
        traineeDao.deleteTrainee(trainee);

        LOGGER.info(String.format(
                "Deleted trainee with username: %s",
                traineeUsername
        ));
    }

    public List<Training> getTraineeTrainings(String username, String password,
                                              String traineeUsername, TrainingSearchCriteria criteria) {
        authService.authenticateUser(username, password);
        traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Trainee with username '%s' was not found", traineeUsername)
                ));
        List<Training> trainings =
                traineeDao.findByTraineeUsernameAndCriteria(traineeUsername, criteria);

        LOGGER.info("Trainings for trainee '{}':", traineeUsername);

        trainings.forEach(t ->
                LOGGER.info(String.format(" - %s", t.getName()))
        );

        return trainings;
    }

    public List<Trainer> getUnassignedTrainers(String username, String password, String traineeUsername) {
        authService.authenticateUser(username, password);
        traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Trainee with username '%s' was not found", traineeUsername)
                ));
        List<Trainer> trainers =
                traineeDao.findUnassignedTrainers(traineeUsername);

        LOGGER.info("Trainers not assigned to trainee '{}':", traineeUsername);

        trainers.forEach(t ->
                LOGGER.info(" - {}", t.getUsername())
        );

        return trainers;
    }

    public void updateTraineeTrainers(String username, String password,
                                      String traineeUsername, List<Trainer> newTrainers) {

        authService.authenticateUser(username, password);
        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Trainee with username '%s' was not found", traineeUsername)
                ));
        List<Trainer> newTrainerList = newTrainers.stream()
                .map(trainer -> trainerDao.findByUsername(trainer.getUsername())
                        .orElseThrow(() -> new IllegalArgumentException(
                                String.format("Trainer not found: %s", trainer.getUsername())
                        )))
                .toList();

        trainee.setTrainers(new ArrayList<>(newTrainerList));
        traineeDao.updateProfile(trainee);

        LOGGER.info("Updated trainers for trainee '{}'", traineeUsername);
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

        LOGGER.debug("Assigned credentials for trainee '{}': username={}", trainee.getFirstName(), username);
    }
}
