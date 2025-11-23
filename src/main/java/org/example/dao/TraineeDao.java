package org.example.dao;

import org.example.entities.Trainee;
import org.example.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class TraineeDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeDao.class);
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void create(Trainee trainee) {
        if (storage.getTraineeMap().containsKey(trainee.getId())) {
            LOGGER.warn("Trainee with id {} already exists. Cannot create.", trainee.getId());
            throw new IllegalArgumentException("Trainee ID already exists");
        }

        Long newId = getNextId();
        trainee.setId(newId);

        storage.getTraineeMap().put(newId, trainee);
        LOGGER.info("Created trainee with id {}", trainee.getId());
    }

    public void update(Trainee trainee) {
        if (!storage.getTraineeMap().containsKey(trainee.getId())) {
            LOGGER.warn("Trainee with id {} does not exist. Cannot update.", trainee.getId());
            throw new IllegalArgumentException("Trainee ID does not exist");
        }
        storage.getTraineeMap().put(trainee.getId(), trainee);
        LOGGER.info("Updated trainee with id {}", trainee.getId());
    }

    public Optional<Trainee> findById(Long id) {
        LOGGER.debug("Finding trainee by id {}", id);
        return Optional.ofNullable(storage.getTraineeMap().get(id));
    }

    public List<Trainee> findAll() {
        LOGGER.debug("Retrieving all trainees from storage");
        return new ArrayList<>(storage.getTraineeMap().values());
    }

    public void delete(Long id) {
        if (!storage.getTraineeMap().containsKey(id)) {
            throw new NoSuchElementException("Trainee with id " + id + " does not exist");
        }
        storage.getTraineeMap().remove(id);
        LOGGER.info("Removed trainee with id {}", id);
    }

    public long countByFullName(String firstName, String lastName) {
        LOGGER.info("countByFullName() invoked for {} {}", firstName, lastName);
        long count = storage.getTraineeMap().values()
                .stream()
                .filter(t -> t.getFirstName().equalsIgnoreCase(firstName)
                        && t.getLastName().equalsIgnoreCase(lastName))
                .count();
        LOGGER.info("Number of trainees by first name '{}' and last name '{}': {}", firstName, lastName, count);
        return count;
    }

    public Long getNextId() {
        return storage.getTraineeMaxId().incrementAndGet();
    }
}
