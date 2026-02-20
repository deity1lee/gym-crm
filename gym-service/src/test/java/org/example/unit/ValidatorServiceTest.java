package org.example.unit;

import org.example.dao.UserDao;
import org.example.entities.TrainingType;
import org.example.entities.User;
import org.example.service.TrainingTypeService;
import org.example.service.ValidatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidatorServiceTest {

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private ValidatorService validatorService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsStrongPasswordValid() {
        assertTrue(validatorService.isStrongPassword("Aa12345678"));
    }

    @Test
    void testIsStrongPasswordTooShort() {
        assertFalse(validatorService.isStrongPassword("Aa123"));
    }

    @Test
    void testIsStrongPasswordMissingUppercase() {
        assertFalse(validatorService.isStrongPassword("aa12345678"));
    }

    @Test
    void testIsStrongPasswordMissingLowercase() {
        assertFalse(validatorService.isStrongPassword("AA12345678"));
    }

    @Test
    void testIsStrongPasswordMissingDigits() {
        assertFalse(validatorService.isStrongPassword("Aaabcdefgh"));
    }

    @Test
    void testValidateFirstNameValid() {
        assertDoesNotThrow(() -> validatorService.validateFirstName("John"));
    }

    @Test
    void testValidateFirstNameEmpty() {
        Optional<String> result = validatorService.validateFirstName("");
        assertTrue(result.isPresent());
        assertEquals("firstName", result.get());
    }

    @Test
    void testValidateFirstNameContainsDigits() {
        Optional<String> result = validatorService.validateFirstName("John5");
        assertTrue(result.isPresent());
        assertEquals("firstName", result.get());
    }

    @Test
    void testValidateLastNameValid() {
        assertDoesNotThrow(() -> validatorService.validateLastName("Doe"));
    }

    @Test
    void testValidateLastNameEmpty() {
        Optional<String> result = validatorService.validateLastName("");
        assertTrue(result.isPresent());
        assertEquals("lastName", result.get());
    }

    @Test
    void testValidateLastNameContainsDigits() {
        Optional<String> result = validatorService.validateLastName("Doe3");
        assertTrue(result.isPresent());
        assertEquals("lastName", result.get());
    }

    @Test
    void testValidateExistingUsernameValid() {
        when(userDao.findByUsername("john")).thenReturn(Optional.of(new User()));
        assertDoesNotThrow(() -> validatorService.validateExistingUsername("john"));
    }

    @Test
    void testValidateExistingUsernameEmptyThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateExistingUsername(""));
    }

    @Test
    void testValidateExistingUsernameNotFoundThrows() {
        when(userDao.findByUsername("ghost")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateExistingUsername("ghost"));
    }

    @Test
    void testValidateTrainingTypeValid() {
        TrainingType input = new TrainingType();
        input.setId(10L);
        input.setTrainingTypeName("Cardio");

        TrainingType dbCopy = new TrainingType();
        dbCopy.setId(10L);
        dbCopy.setTrainingTypeName("Cardio");

        when(trainingTypeService.getTrainingTypeById(10L)).thenReturn(dbCopy);

        assertDoesNotThrow(() -> validatorService.validateTrainingType(input));
    }

    @Test
    void testValidateTrainingTypeNullThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateTrainingType(null));
    }

    @Test
    void testValidateTrainingTypeInvalidIdThrows() {
        TrainingType type = new TrainingType();
        type.setId(0L);
        type.setTrainingTypeName("Boxing");

        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateTrainingType(type));
    }

    @Test
    void testValidateTrainingTypeMissingNameThrows() {
        TrainingType type = new TrainingType();
        type.setId(5L);
        type.setTrainingTypeName("");

        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateTrainingType(type));
    }

    @Test
    void testValidateTrainingTypeNameMismatchThrows() {
        TrainingType input = new TrainingType();
        input.setId(5L);
        input.setTrainingTypeName("Yoga");

        TrainingType dbCopy = new TrainingType();
        dbCopy.setId(5L);
        dbCopy.setTrainingTypeName("Pilates");

        when(trainingTypeService.getTrainingTypeById(5L)).thenReturn(dbCopy);

        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateTrainingType(input));
    }

    @Test
    void testValidateUserSuccess() {
        User user = new User();
        user.setUsername("john");
        user.setFirstName("John");
        user.setLastName("Doe");

        when(userDao.findByUsername("john")).thenReturn(Optional.of(new User()));

        assertDoesNotThrow(() -> validatorService.validate(user));
    }

    @Test
    void testValidateUserUsernameNotFoundThrows() {
        User user = new User();
        user.setUsername("ghost");
        user.setFirstName("A");
        user.setLastName("B");

        when(userDao.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateExistingUsername(user.getUsername()));
    }
}
