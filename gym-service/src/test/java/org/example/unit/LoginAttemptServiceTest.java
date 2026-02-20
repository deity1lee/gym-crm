package org.example.unit;

import org.example.entities.User;
import org.example.exception.BruteForceAttemptException;
import org.example.service.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.example.utilities.SecurityConstants.LOCK_TIME_MINUTES;

class LoginAttemptServiceTest {

    @InjectMocks
    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUnlockIfNeededAlreadyUnlockedDoesNothing() {
        User user = new User();
        user.setAccountNonLocked(true);

        assertDoesNotThrow(() ->
                loginAttemptService.unlockIfNeeded(user)
        );

        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testUnlockIfNeededLockTimeNotExpiredThrows() {
        User user = new User();
        user.setAccountNonLocked(false);
        user.setFailedAttempts(5);
        user.setLockTime(LocalDateTime.now());

        assertThrows(
                BruteForceAttemptException.class,
                () -> loginAttemptService.unlockIfNeeded(user)
        );

        assertFalse(user.isAccountNonLocked());
    }

    @Test
    void testUnlockIfNeededLockTimeExpiredUnlocksUser() {
        User user = new User();
        user.setAccountNonLocked(false);
        user.setFailedAttempts(5);
        user.setLockTime(
                LocalDateTime.now().minusMinutes(LOCK_TIME_MINUTES + 1)
        );

        assertDoesNotThrow(() ->
                loginAttemptService.unlockIfNeeded(user)
        );

        assertTrue(user.isAccountNonLocked());
        assertEquals(0, user.getFailedAttempts());
        assertNull(user.getLockTime());
    }
}
