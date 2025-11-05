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

    private static final Logger logger = LoggerFactory.getLogger(TraineeDao.class);
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void save(Trainee trainee) {
        storage.getTraineeMap().put(trainee.getId(), trainee);
        logger.debug("Saved trainee with id {} to in-memory storage", trainee.getId());
    }

    public Optional<Trainee> findById(Long id) {
        logger.debug("Finding trainee by id {}", id);
        return Optional.ofNullable(storage.getTraineeMap().get(id));
    }

    public List<Trainee> findAll() {
        logger.debug("Retrieving all trainees from storage");
        return new ArrayList<>(storage.getTraineeMap().values());
    }

    public void delete(Long id) {
        storage.getTraineeMap().remove(id);
        logger.debug("Removed trainee with id {}", id);
    }

    public long countByFullName(String first, String last) {
        logger.info("countByFullName() invoked for {} {}", first, last);
        long count = storage.getTraineeMap().values()
                .stream()
                .filter(t -> t.getFirstName().equalsIgnoreCase(first)
                        && t.getLastName().equalsIgnoreCase(last))
                .count();
        logger.info("Number of trainees by first name '{}' and last name '{}': {}", first, last, count);
        return count;
    }
}
