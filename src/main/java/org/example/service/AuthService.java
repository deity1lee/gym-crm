package org.example.service;

import org.example.dao.UserDao;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<Trainee> authenticateTrainee(String username, String password) {
        return userDao.findByUsername(username)
                .filter(u -> u instanceof Trainee)
                .filter(u -> u.getPassword().equals(password))
                .map(u -> (Trainee) u);
    }
    public Optional<Trainer> authenticateTrainer(String username, String password) {
        return userDao.findByUsername(username)
                .filter(u -> u instanceof Trainer)
                .filter(u -> u.getPassword().equals(password))
                .map(u -> (Trainer) u);
    }
    public Optional<User> authenticateUser(String username, String password) {
        return userDao.findByUsername(username)
                .filter(u -> u.getPassword().equals(password));
    }
}
