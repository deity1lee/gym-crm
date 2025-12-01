package org.example.service;

import org.example.entities.User;
import org.springframework.stereotype.Service;

@Service
public class ValidatorService {
    public boolean isStrongPassword(String password) {
        if (password.length() == 10 && password.matches(".*\\d.*") &&
                password.matches(".*[a-z].*") && password.matches(".*[A-Z].*"))
        {
            return true;
        }
        return false;
    }

    public void validateFirstName(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }

        if (firstName.matches(".*\\d.*")) {
            throw new IllegalArgumentException("First name cannot contain digits: " + firstName);
        }
    }

    public void validateLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }

        if (lastName.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Last name cannot contain digits: " + lastName);
        }
    }

    public void validate(User user) {
        validateFirstName(user.getFirstName());
        validateLastName(user.getLastName());
    }
}
