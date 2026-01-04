package auth;

import org.example.dao.UserDao;
import org.example.entities.User;
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
    void testAuthenticateUser_Success() {
        String username = "john";
        String password = "pass123";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));

        User result = authService.authenticateUser(username, password);

        assertEquals(user, result);
        verify(userDao, times(1)).findByUsername(username);
    }

    @Test
    void testAuthenticateUser_InvalidPassword_ThrowsException() {
        String username = "john";
        String correctPassword = "pass123";
        String wrongPassword = "wrongPass";

        User user = new User();
        user.setUsername(username);
        user.setPassword(correctPassword);

        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.authenticateUser(username, wrongPassword)
        );

        assertTrue(ex.getMessage().contains("Not authenticated user"));
    }

    @Test
    void testAuthenticateUser_UserNotFound_ThrowsException() {
        String username = "unknown";
        String password = "pass";

        when(userDao.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.authenticateUser(username, password)
        );
    }
}
