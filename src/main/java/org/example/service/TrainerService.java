package org.example.service;

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

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    private TrainerDao trainerDao;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    private void assignCredentials(Trainer trainer) {
        String username = CredentialGenerator.generateUsername(
                trainer.getFirstName(),
                trainer.getLastName(),
                trainerDao.countByFullName(trainer.getFirstName(), trainer.getLastName())
        );

        String password = CredentialGenerator.generateRandomPassword();

        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setActive(true);

        logger.debug("Assigned credentials for {}: username={}, password={}",
                trainer.getFirstName(), username, password);
    }

    public void createTrainer(Trainer trainer) {
        assignCredentials(trainer);
        trainerDao.save(trainer);
        logger.info("Created trainer profile for username: {}", trainer.getUsername());
    }

    public void updateTrainer(Trainer trainer) {
        trainerDao.save(trainer);
        logger.info("Updated trainer with id: {}", trainer.getId());
    }

    public Optional<Trainer> getTrainerById(Long id) {
        logger.debug("Fetching trainer with id: {}", id);
        return trainerDao.findById(id);
    }

    public List<Trainer> getAllTrainers() {
        logger.debug("Fetching all trainers");
        return trainerDao.findAll();
    }
}

