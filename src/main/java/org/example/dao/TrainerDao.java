package org.example.dao;

import org.example.entities.Trainer;
import org.example.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class TrainerDao {

    private static final Logger logger = LoggerFactory.getLogger(TrainerDao.class);
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void save(Trainer trainer) {
        storage.getTrainerMap().put(trainer.getId(), trainer);
        logger.debug("Saved trainer with id {} to in-memory storage", trainer.getId());
    }

    public Optional<Trainer> findById(Long id) {
        logger.debug("Finding trainer by id {}", id);
        return Optional.ofNullable(storage.getTrainerMap().get(id));
    }

    public List<Trainer> findAll() {
        logger.debug("Retrieving all trainers from storage");
        return new ArrayList<>(storage.getTrainerMap().values());
    }

    public long countByFullName(String first, String last) {
        logger.info("countByFullName() invoked for {} {}", first, last);
        long count = storage.getTrainerMap().values()
                .stream()
                .filter(t -> t.getFirstName().equalsIgnoreCase(first)
                        && t.getLastName().equalsIgnoreCase(last))
                .count();
        logger.info("Number of trainers by first name '{}' and last name '{}': {}", first, last, count);
        return count;
    }
}

