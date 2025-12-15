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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testChangePasswordSuccess() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        when(authenticationManager.authenticate(any()))
                .thenReturn(mock(Authentication.class));

        when(userDao.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(validatorService.isStrongPassword("newStrong"))
                .thenReturn(true);

        when(passwordEncoder.encode("newStrong"))
                .thenReturn("encodedNew");

        authService.changePassword("john", "old", "newStrong");

        verify(userDao).updatePassword(1L, "encodedNew");
    }

    @Test
    void testChangePasswordFailsWeakPasswordNoUpdate() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        when(authenticationManager.authenticate(any()))
                .thenReturn(mock(Authentication.class));

        when(userDao.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(validatorService.isStrongPassword("weak"))
                .thenReturn(false);

        authService.changePassword("john", "old", "weak");

        verify(userDao, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void testChangePasswordFailsWrongOldPassword() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new UnauthorizedException("Invalid credentials"));

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
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPwd");

        authService.assignCredentials(trainee);

        assertNotNull(trainee.getUsername());
        assertNotNull(trainee.getPassword()); // encoded password
        assertTrue(trainee.isActive());
    }
}
