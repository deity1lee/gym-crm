package unit;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.dao.UserDao;
import org.example.entities.Trainee;
import org.example.entities.User;
import org.example.service.AuthService;
import org.example.service.ValidatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserDao userDao;
    private ValidatorService validatorService;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        validatorService = mock(ValidatorService.class);
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);

        authService = new AuthService(userDao);
        authService.setValidatorService(validatorService);
        authService.setTraineeDao(traineeDao);
        authService.setTrainerDao(trainerDao);
    }

    @Test
    void authenticateUser_Success() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("123");

        when(userDao.findByUsername("john")).thenReturn(Optional.of(user));

        User result = authService.authenticateUser("john", "123");

        assertEquals("john", result.getUsername());
    }

    @Test
    void authenticateUser_FailsWrongPassword() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("correct");

        when(userDao.findByUsername("john")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> authService.authenticateUser("john", "wrong"));
    }

    @Test
    void authenticateUser_FailsUserNotFound() {
        when(userDao.findByUsername("absent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authService.authenticateUser("absent", "pass"));
    }

    @Test
    void changePassword_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("old");

        when(userDao.findByUsername("john")).thenReturn(Optional.of(user));
        when(validatorService.isStrongPassword("newStrong")).thenReturn(true);

        authService.changePassword("john", "old", "newStrong");

        verify(userDao).updatePassword(1L, "newStrong");
    }

    @Test
    void changePassword_FailsWeakPassword_NoUpdate() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("old");

        when(userDao.findByUsername("john")).thenReturn(Optional.of(user));
        when(validatorService.isStrongPassword("weak")).thenReturn(false);

        authService.changePassword("john", "old", "weak");

        verify(userDao, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void changePassword_FailsWrongOldPassword() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("correct");

        when(userDao.findByUsername("john")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> authService.changePassword("john", "wrong", "newPass"));

        verify(userDao, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void assignCredentials_AssignsUsernamePasswordAndActivates() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(traineeDao.countByFullName("John", "Doe")).thenReturn(1L);
        when(trainerDao.countByFullName("John", "Doe")).thenReturn(2L);

        authService.assignCredentials(trainee);

        assertNotNull(trainee.getUsername());
        assertNotNull(trainee.getPassword());
        assertTrue(trainee.isActive());
    }
}
