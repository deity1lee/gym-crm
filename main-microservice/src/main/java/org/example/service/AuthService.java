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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Optional;

@Transactional
@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private UserDao userDao;
    private ValidatorService validatorService;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;

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

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        String tx = MDC.get("transactionId");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, oldPassword)
            );
        } catch (AuthenticationException ex) {
            LOGGER.warn("[{}] Authentication failed: invalid credentials", tx);
            throw new UnauthorizedException("Invalid credentials");
        }        if (!validatorService.isStrongPassword(newPassword)) {
            LOGGER.warn("[{}] Password was not updated. Provided password is not strong", tx);
            throw new IllegalArgumentException("Provided password is not strong");
        }
        User user = findByUsername(username).orElseThrow(() -> new UnauthorizedException("User not found"));
        String encoded = passwordEncoder.encode(newPassword);
        userDao.updatePassword(user.getId(), encoded);
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
        user.setRawPassword(password);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);

        String tx = MDC.get("transactionId");
        LOGGER.info("[{}] Assigned credentials for trainee with name {} and lastname {}."
                ,tx, user.getFirstName(), user.getLastName());
    }

    public Optional<User> findByUsername(String username) {
         return userDao.findByUsername(username);
    }

    public void updateUser(User user) {
        userDao.save(user);
    }
}
