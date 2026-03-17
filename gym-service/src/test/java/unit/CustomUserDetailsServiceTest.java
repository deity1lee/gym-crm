package unit;

import org.example.dao.UserDao;
import org.example.entities.User;
import org.example.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsernameSuccessActiveUser() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("password");
        user.setActive(true);

        when(userDao.findByUsername("john"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("john");

        assertNotNull(userDetails);
        assertEquals("john", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.isAccountNonLocked());

        verify(userDao).findByUsername("john");
    }

    @Test
    void testLoadUserByUsernameInactiveUserShouldBeLocked() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("password");
        user.setActive(false);

        when(userDao.findByUsername("john"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("john");

        assertNotNull(userDetails);
        assertFalse(userDetails.isAccountNonLocked());

        verify(userDao).findByUsername("john");
    }

    @Test
    void testLoadUserByUsernameUserNotFound() {
        when(userDao.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("missing")
        );

        verify(userDao).findByUsername("missing");
    }
}
