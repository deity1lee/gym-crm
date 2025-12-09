package org.example.service;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.dao.UserDao;
import org.example.entities.User;
import org.example.exception.UnauthorizedException;
import org.example.utilities.CredentialGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private UserDao userDao;
    private ValidatorService validatorService;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setValidatorService(ValidatorService validatorService) {
        this.validatorService = validatorService;
    }

    public User authenticateUser(String username, String password) {
        String tx = MDC.get("transactionId");

        return userDao.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(() -> {
                    LOGGER.warn("[{}] Authentication failed: invalid credentials", tx);
                    return new UnauthorizedException("Invalid credentials");
                });
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        String tx = MDC.get("transactionId");
        User user = authenticateUser(username, oldPassword);
        if (!validatorService.isStrongPassword(newPassword)) {
            LOGGER.warn("[{}] Password was not updated. Provided password is not strong", tx);
            return;
        }
        user.setPassword(newPassword);
        userDao.updatePassword(user.getId(), newPassword);
        LOGGER.info("[{}] Password was changed successfully!", tx);
    }

    public void assignCredentials(User user) {
        long traineeCount = traineeDao.countByFullName(user.getFirstName(), user.getLastName());
        long trainerCount = trainerDao.countByFullName(user.getFirstName(), user.getLastName());
        long total = traineeCount + trainerCount;

        String username = CredentialGenerator.generateUsername(
                user.getFirstName(),
                user.getLastName(),
                total
        );

        String password = CredentialGenerator.generateRandomPassword();

        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        String tx = MDC.get("transactionId");
        LOGGER.info("[{}] Assigned credentials for user with name {} and lastname {}."
                ,tx, user.getFirstName(), user.getLastName());
    }
}
