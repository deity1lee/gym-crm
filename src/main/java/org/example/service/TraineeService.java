package org.example.service;

import org.example.dao.TraineeDao;
import org.example.entities.Trainee;
import org.example.utilities.CredentialGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Service
public class TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private TraineeDao traineeDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    private void assignCredentials(Trainee trainee) {
        String username = CredentialGenerator.generateUsername(
                trainee.getFirstName(),
                trainee.getLastName(),
                traineeDao.countByFullName(trainee.getFirstName(), trainee.getLastName())
        );

        String password = CredentialGenerator.generateRandomPassword();

        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setActive(true);

        logger.debug("Assigned credentials for {}: username={}, password={}",
                trainee.getFirstName(), username, password);
    }

    public void createTrainee(Trainee trainee) {
        assignCredentials(trainee);
        traineeDao.save(trainee);
        logger.info("Created trainee profile for username: {}", trainee.getUsername());
    }

    public void updateTrainee(Trainee trainee) {
        traineeDao.save(trainee);
        logger.info("Updated trainee with id: {}", trainee.getId());
    }

    public Optional<Trainee> getTraineeById(Long id) {
        logger.debug("Fetching trainee with id: {}", id);
        return traineeDao.findById(id);
    }

    public List<Trainee> getAllTrainees() {
        logger.debug("Fetching all trainees");
        return traineeDao.findAll();
    }

    public void deleteTrainee(Long id) {
        logger.info("Deleted trainee with id: {}", id);
        traineeDao.delete(id);
    }
}
