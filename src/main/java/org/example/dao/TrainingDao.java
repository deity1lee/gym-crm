package org.example.dao;

import org.example.entities.Training;
import org.example.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class TrainingDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingDao.class);
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void save(Training training) {
        storage.getTrainingMap().put(training.getId(), training);
        LOGGER.info("Saved training with name {} to in-memory storage", training.getName());
    }

    public Optional<Training> findById(Long id) {
        LOGGER.debug("Finding trainings by id {}", id);
        return Optional.ofNullable(storage.getTrainingMap().get(id));
    }

    public List<Training> findAll() {
        LOGGER.debug("Retrieving all trainings from storage");
        return new ArrayList<>(storage.getTrainingMap().values());
    }
}

