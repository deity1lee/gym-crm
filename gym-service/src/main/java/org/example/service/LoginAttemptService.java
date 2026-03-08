package org.example.service;

import org.example.exception.BruteForceAttemptException;
import org.springframework.stereotype.Service;
import org.example.entities.User;
import java.time.LocalDateTime;

import static org.example.utilities.SecurityConstants.LOCK_TIME_MINUTES;

@Service
public class LoginAttemptService {

    public void unlockIfNeeded(User user) throws BruteForceAttemptException {
        if (user.isAccountNonLocked()) {
            return;
        }

        LocalDateTime unlockTime =
                user.getLockTime().plusMinutes(LOCK_TIME_MINUTES);

        if (LocalDateTime.now().isBefore(unlockTime)) {
            throw new BruteForceAttemptException(
                    "Too many unsuccessful login attempts. Try again later."
            );
        }
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        user.setLockTime(null);
    }
}
