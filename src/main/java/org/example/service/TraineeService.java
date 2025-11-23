package org.example.service;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.entities.Trainee;
import org.example.utilities.CredentialGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Service
public class TraineeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    public void createTrainee(Trainee trainee) {
        if (trainee.getId() != null) {
            throw new IllegalArgumentException("New trainee must not have an id. Use updateTrainee() for existing trainees.");
        }
        assignCredentials(trainee);
        traineeDao.create(trainee);
        LOGGER.info("Created trainee profile for username: {}", trainee.getUsername());
    }

    public void updateTrainee(Trainee trainee) {
        Optional.ofNullable(trainee.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cannot update trainee without id. Use createTrainee() for new trainees"));
        traineeDao.update(trainee);
        LOGGER.info("Updated trainee with id: {}", trainee.getId());
    }

    public Optional<Trainee> getTraineeById(Long id) {
        LOGGER.debug("Fetching trainee with id: {}", id);
        return traineeDao.findById(id);
    }

    public List<Trainee> getAllTrainees() {
        LOGGER.debug("Fetching all trainees");
        return traineeDao.findAll();
    }

    public void deleteTrainee(Long id) {
        try {
            traineeDao.delete(id);
            LOGGER.info("Deleted trainee with id: {}", id);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Trainee with provided ID does not exist");
        }
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
