package unit;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.dao.UserDao;
import org.example.entities.Trainee;
import org.example.entities.User;
import org.example.exception.UnauthorizedException;
import org.example.service.AuthService;
import org.example.service.ValidatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserDao userDao;
    @Mock
    private ValidatorService validatorService;
    @Mock
    private TraineeDao traineeDao;
    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthenticateUserSuccess() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("123");

        when(userDao.findByUsername("john")).thenReturn(Optional.of(user));

        User result = authService.authenticateUser("john", "123");

        assertEquals("john", result.getUsername());
    }

    @Test
    void testAuthenticateUserFailsWrongPassword() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("correct");

        when(userDao.findByUsername("john")).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class,
                () -> authService.authenticateUser("john", "wrong"));
    }

    @Test
    void testAuthenticateUserFailsUserNotFound() {
        when(userDao.findByUsername("absent")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class,
                () -> authService.authenticateUser("absent", "pass"));
    }

    @Test
    void testChangePasswordSuccess() {
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
    void testChangePasswordFailsWeakPasswordNoUpdate() {
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
    void testChangePasswordFailsWrongOldPassword() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("correct");

        when(userDao.findByUsername("john")).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class,
                () -> authService.changePassword("john", "wrong", "newPass"));

        verify(userDao, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void testAssignCredentialsAssignsUsernamePasswordAndActivates() {
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
