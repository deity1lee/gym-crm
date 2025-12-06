package org.example.service;

import org.example.dao.UserDao;
import org.example.entities.TrainingType;
import org.example.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorService.class);
    private TrainingTypeService trainingTypeService;
    private UserDao userDao;

    @Autowired
    public void setTrainingTypeService(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean isStrongPassword(String password) {
        String txId = MDC.get("transactionId");
        if (password.length() == 10 && password.matches(".*\\d.*") &&
                password.matches(".*[a-z].*") && password.matches(".*[A-Z].*"))
        {
            return true;
        }
        LOGGER.warn("[{}] Not a strong password!", txId);
        return false;
    }

    public void validateFirstName(String firstName) {
        String txId = MDC.get("transactionId");
        if (firstName == null || firstName.isEmpty()) {
            LOGGER.warn("[{}] First name cannot be empty", txId);
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (firstName.matches(".*\\d.*")) {
            LOGGER.warn("First name cannot contain digits: {}", firstName);
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public void validateLastName(String lastName) {
        String txId = MDC.get("transactionId");
        if (lastName == null || lastName.isEmpty()) {
            LOGGER.warn("{[]} Last name cannot be empty", txId);
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (lastName.matches(".*\\d.*")) {
            LOGGER.warn("Last name cannot contain digits: {}", lastName);
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public void validateExistingUsername(String username) {
        String txId = MDC.get("transactionId");

        if (username == null || username.isBlank()) {
            LOGGER.warn("[{}] Username is empty", txId);
            throw new IllegalArgumentException("Username cannot be empty");
        }

        boolean exists = userDao.findByUsername(username).isPresent();

        if (!exists) {
            LOGGER.warn("[{}] Username '{}' does not exist in DB", txId, username);
            throw new IllegalArgumentException("User does not exist");
        }

        LOGGER.debug("[{}] Username '{}' exists", txId, username);
    }

    public void validateTrainingType(TrainingType trainingType) {
        String txId = MDC.get("transactionId");

        if (trainingType == null) {
            LOGGER.warn("[{}] Training type is missing", txId);
            throw new IllegalArgumentException("Training type is required");
        }

        if (trainingType.getId() == null || trainingType.getId() <= 0) {
            LOGGER.warn("[{}] Invalid TrainingType.id: {}", txId, trainingType.getId());
            throw new IllegalArgumentException("Training type ID can only be positive integer and not null");
        }

        if (trainingType.getTrainingTypeName() == null || trainingType.getTrainingTypeName().isBlank()) {
            LOGGER.warn("[{}] TrainingType name is empty", txId);
            throw new IllegalArgumentException("Training type name cannot be empty");
        }

        TrainingType entity = trainingTypeService.getTrainingTypeById(trainingType.getId());

        if (!entity.getTrainingTypeName().equalsIgnoreCase(trainingType.getTrainingTypeName())) {
            LOGGER.warn(
                    "[{}] TrainingType mismatch: DB(name={}) != Request(name={})",
                    txId, entity.getTrainingTypeName(), trainingType.getTrainingTypeName()
            );
            throw new IllegalArgumentException("Training type does not match DB record");
        }

        LOGGER.debug("[{}] TrainingType validated: id={}, name={}", txId, entity.getId(), entity.getTrainingTypeName());
    }

    public void validate(User user) {
        validateFirstName(user.getFirstName());
        validateLastName(user.getLastName());
    }
}
