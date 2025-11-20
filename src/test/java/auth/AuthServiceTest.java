package auth;

import org.example.dao.UserDao;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private UserDao userDao;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        authService = new AuthService(userDao);
    }

    @Test
    void authenticateTrainee_Success() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setPassword("pass123");

        when(userDao.findByUsername("john.doe"))
                .thenReturn(Optional.of(trainee));

        Optional<Trainee> result =
                authService.authenticateTrainee("john.doe", "pass123");

        assertTrue(result.isPresent());
        assertEquals("john.doe", result.get().getUsername());
    }

    @Test
    void authenticateTrainee_FailsWrongPassword() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setPassword("correct");

        when(userDao.findByUsername("john.doe"))
                .thenReturn(Optional.of(trainee));

        Optional<Trainee> result =
                authService.authenticateTrainee("john.doe", "wrong");

        assertTrue(result.isEmpty());
    }

    @Test
    void authenticateTrainee_FailsUserIsTrainerNotTrainee() {
        Trainer trainer = new Trainer();
        trainer.setUsername("john");
        trainer.setPassword("pass");

        when(userDao.findByUsername("john"))
                .thenReturn(Optional.of(trainer));

        Optional<Trainee> result =
                authService.authenticateTrainee("john", "pass");

        assertTrue(result.isEmpty());
    }

    @Test
    void authenticateTrainee_FailsUserNotFound() {
        when(userDao.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        Optional<Trainee> result =
                authService.authenticateTrainee("unknown", "any");

        assertTrue(result.isEmpty());
    }

    @Test
    void authenticateTrainer_Success() {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainerA");
        trainer.setPassword("abc123");

        when(userDao.findByUsername("trainerA"))
                .thenReturn(Optional.of(trainer));

        Optional<Trainer> result =
                authService.authenticateTrainer("trainerA", "abc123");

        assertTrue(result.isPresent());
        assertEquals("trainerA", result.get().getUsername());
    }

    @Test
    void authenticateTrainer_FailsWrongPassword() {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainerA");
        trainer.setPassword("correct");

        when(userDao.findByUsername("trainerA"))
                .thenReturn(Optional.of(trainer));

        Optional<Trainer> result =
                authService.authenticateTrainer("trainerA", "wrong");

        assertTrue(result.isEmpty());
    }

    @Test
    void authenticateTrainer_FailsUserIsTraineeNotTrainer() {
        Trainee trainee = new Trainee();
        trainee.setUsername("someone");
        trainee.setPassword("123");

        when(userDao.findByUsername("someone"))
                .thenReturn(Optional.of(trainee));

        Optional<Trainer> result =
                authService.authenticateTrainer("someone", "123");

        assertTrue(result.isEmpty());
    }

    @Test
    void authenticateTrainer_FailsUserNotFound() {
        when(userDao.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        Optional<Trainer> result =
                authService.authenticateTrainer("unknown", "pass");

        assertTrue(result.isEmpty());
    }
}

