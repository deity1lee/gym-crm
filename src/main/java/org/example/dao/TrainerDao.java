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

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerDao.class);
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void create(Trainer trainer) {
        if (storage.getTrainerMap().containsKey(trainer.getId())) {
            LOGGER.warn("Trainer with id {} already exists. Cannot create.", trainer.getId());
            throw new IllegalArgumentException("Trainer ID already exists");
        }

        Long newId = getNextId();
        trainer.setId(newId);

        storage.getTrainerMap().put(newId, trainer);
        LOGGER.info("Created trainer with id {}", trainer.getId());
    }

    public void update(Trainer trainer) {
        if (!storage.getTrainerMap().containsKey(trainer.getId())) {
            LOGGER.warn("Trainer with id {} does not exist. Cannot update.", trainer.getId());
            throw new IllegalArgumentException("Trainer ID does not exist");
        }
        storage.getTrainerMap().put(trainer.getId(), trainer);
        LOGGER.info("Updated trainer with id {}", trainer.getId());
    }

    public Optional<Trainer> findById(Long id) {
        LOGGER.debug("Finding trainer by id {}", id);
        return Optional.ofNullable(storage.getTrainerMap().get(id));
    }

    public List<Trainer> findAll() {
        LOGGER.debug("Retrieving all trainers from storage");
        return new ArrayList<>(storage.getTrainerMap().values());
    }

    public long countByFullName(String firstName, String lastName) {
        LOGGER.info("countByFullName() invoked for {} {}", firstName, lastName);
        long count = storage.getTrainerMap().values()
                .stream()
                .filter(t -> t.getFirstName().equalsIgnoreCase(firstName)
                        && t.getLastName().equalsIgnoreCase(lastName))
                .count();
        LOGGER.info("Number of trainers by first name '{}' and last name '{}': {}", firstName, lastName, count);
        return count;
    }
    public Long getNextId() {
        return storage.getTrainerMap().keySet().stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }
}

