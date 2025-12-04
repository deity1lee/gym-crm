package org.example.service;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.entities.Trainer;
import org.example.utilities.CredentialGenerator;
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

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    public void createTrainer(Trainer trainer) {
        if (trainer.getId() != null) {
            throw new IllegalArgumentException("New trainer must not have an id. Use updateTrainer() for existing trainers.");
        }
        assignCredentials(trainer);
        trainerDao.create(trainer);
        LOGGER.info("Created trainer profile for username: {}", trainer.getUsername());
    }

    public void updateTrainer(Trainer trainer) {
        if (trainer.getId() == null) {
            throw new IllegalArgumentException("Cannot update trainee without id. Use createTrainee() for new trainees.");
        }
        trainerDao.update(trainer);
        LOGGER.info("Updated trainer with id: {}", trainer.getId());
    }

    public Optional<Trainer> getTrainerById(Long id) {
        LOGGER.debug("Fetching trainer with id: {}", id);
        return trainerDao.findById(id);
    }

    public List<Trainer> getAllTrainers() {
        LOGGER.debug("Fetching all trainers");
        return trainerDao.findAll();
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

