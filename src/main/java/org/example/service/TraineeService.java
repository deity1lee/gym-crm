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

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);

    private TraineeDao traineeDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    public void createTrainee(Trainee trainee) {
        if (traineeDao.findById(trainee.getId()).isPresent()) {
            LOGGER.warn("Trainee with id {} already exists. Creation skipped.", trainee.getId());
            return;
        }
        assignCredentials(trainee);
        traineeDao.save(trainee);
        LOGGER.info("Created trainee profile for username: {}", trainee.getUsername());
    }


    public void updateTrainee(Trainee trainee) {
        traineeDao.save(trainee);
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
        if (traineeDao.findById(id).isEmpty()) {
            LOGGER.warn("Trainee with id {} does not exist. Removal skipped.", id);
            return;
        }
        traineeDao.delete(id);
        LOGGER.info("Deleted trainee with id: {}", id);
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

        LOGGER.debug("Assigned credentials for {}: username={}, password={}",
                trainee.getFirstName(), username, password);
    }
}
