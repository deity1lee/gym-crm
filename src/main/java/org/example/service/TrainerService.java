package org.example.service;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.utilities.CredentialGenerator;
import org.example.utilities.TrainingSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
        validatorService.validate(trainer);
        assignCredentials(trainer);
        trainerDao.create(trainer);

        LOGGER.info(String.format(
                "Created trainer profile for username: %s",
                trainer.getUsername()
        ));
    }

    public Trainer selectTrainerByUsername(String username, String password, String trainerUsernameForSearch) {
        authService.authenticateUser(username, password);

        Trainer trainer = trainerDao.findByUsername(trainerUsernameForSearch)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Trainer with username '%s' was not found", trainerUsernameForSearch)
                ));

        LOGGER.info(String.format(
                "Trainer with username '%s' was found",
                trainerUsernameForSearch
        ));

        return trainer;
    }

    public void changePassword(String username, String password, String trainerUsername,
                               String oldPassword, String newPassword) {

        authService.authenticateUser(username, password);
        Trainer trainer = selectTrainerByUsername(trainerUsername, oldPassword, trainerUsername);

        if (!validatorService.isStrongPassword(newPassword)) {
            LOGGER.info("Password was not updated. Provided password is not strong");
            return;
        }

        trainer.setPassword(newPassword);
        trainerDao.updatePassword(trainer.getId(), newPassword);

        LOGGER.info("Password was changed successfully");
    }

    public void updateTrainer(String username, String password, Trainer newTrainer) {
        validatorService.validate(newTrainer);
        authService.authenticateUser(username, password);

        Trainer existing = trainerDao.findByUsername(newTrainer.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Trainer with username '%s' was not found", newTrainer.getUsername())
                ));

        existing.setFirstName(newTrainer.getFirstName());
        existing.setLastName(newTrainer.getLastName());

        trainerDao.updateProfile(existing);

        LOGGER.info(String.format(
                "Updated trainer profile for username: %s",
                existing.getUsername()
        ));
    }

    public void activateTrainer(String username, String password, String trainerUsername) {
        authService.authenticateUser(username, password);

        Trainer trainer = trainerDao.findByUsername(trainerUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Trainer with username '%s' was not found", trainerUsername)
                ));

        trainerDao.activateTrainer(trainer);

        LOGGER.info(String.format(
                "Activated trainer with username: %s",
                trainerUsername
        ));
    }

    public void deActivateTrainer(String username, String password, String trainerUsername) {
        authService.authenticateUser(username, password);

        Trainer trainer = trainerDao.findByUsername(trainerUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Trainer with username '%s' was not found", trainerUsername)
                ));

        trainerDao.deactivateTrainer(trainer);

        LOGGER.info(String.format(
                "Deactivated trainer with username: %s",
                trainerUsername
        ));
    }

    public List<Training> getTrainerTrainings(String username, String password,
                                              String trainerUsername, TrainingSearchCriteria criteria) {

        authService.authenticateUser(username, password);

        trainerDao.findByUsername(trainerUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Trainer with username '%s' was not found", trainerUsername)
                ));

        List<Training> trainings =
                trainerDao.findByTrainerUsernameAndCriteria(trainerUsername, criteria);

        LOGGER.info(String.format(
                "Trainings for trainer '%s':",
                trainerUsername
        ));

        trainings.forEach(t ->
                LOGGER.info(String.format(" - %s", t.getName()))
        );

        return trainings;
    }

    private void assignCredentials(Trainer trainer) {
        long traineeCount =
                traineeDao.countByFullName(trainer.getFirstName(), trainer.getLastName());
        long trainerCount =
                trainerDao.countByFullName(trainer.getFirstName(), trainer.getLastName());

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

        LOGGER.debug(String.format(
                "Assigned credentials for trainer '%s': username=%s",
                trainer.getFirstName(),
                username
        ));
    }
}
