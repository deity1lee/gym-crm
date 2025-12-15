package org.example.service;

import org.example.dao.UserDao;
import org.example.entities.User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User authenticateUser(String username, String password) {
        return userDao.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                String.format("Not authenticated user: %s", username)));
    }
}
