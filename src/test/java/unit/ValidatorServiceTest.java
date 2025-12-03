package unit;

import org.example.dao.UserDao;
import org.example.entities.TrainingType;
import org.example.entities.User;
import org.example.service.TrainingTypeService;
import org.example.service.ValidatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.slf4j.MDC;

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
        MDC.put("transactionId", "test-tx");
    }

    @Test
    void isStrongPassword_valid() {
        assertTrue(validatorService.isStrongPassword("Aa12345678"));
    }

    @Test
    void isStrongPassword_invalidTooShort() {
        assertFalse(validatorService.isStrongPassword("Aa123"));
    }

    @Test
    void isStrongPassword_missingUppercase() {
        assertFalse(validatorService.isStrongPassword("aa12345678"));
    }

    @Test
    void isStrongPassword_missingLowercase() {
        assertFalse(validatorService.isStrongPassword("AA12345678"));
    }

    @Test
    void isStrongPassword_missingDigits() {
        assertFalse(validatorService.isStrongPassword("Aaabcdefgh"));
    }

    // ---------------------------------------------------------
    // validateFirstName()
    // ---------------------------------------------------------

    @Test
    void validateFirstName_valid() {
        assertDoesNotThrow(() -> validatorService.validateFirstName("John"));
    }

    @Test
    void validateFirstName_empty_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateFirstName(""));
    }

    @Test
    void validateFirstName_containsDigits_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateFirstName("John5"));
    }

    // ---------------------------------------------------------
    // validateLastName()
    // ---------------------------------------------------------

    @Test
    void validateLastName_valid() {
        assertDoesNotThrow(() -> validatorService.validateLastName("Doe"));
    }

    @Test
    void validateLastName_empty_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateLastName(""));
    }

    @Test
    void validateLastName_containsDigits_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateLastName("Doe3"));
    }

    // ---------------------------------------------------------
    // validateExistingUsername()
    // ---------------------------------------------------------

    @Test
    void validateExistingUsername_valid() {
        when(userDao.findByUsername("john")).thenReturn(Optional.of(new User()));

        assertDoesNotThrow(() ->
                validatorService.validateExistingUsername("john")
        );
    }

    @Test
    void validateExistingUsername_empty_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateExistingUsername(""));
    }

    @Test
    void validateExistingUsername_notFound_throws() {
        when(userDao.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateExistingUsername("ghost"));
    }

    // ---------------------------------------------------------
    // validateTrainingType()
    // ---------------------------------------------------------

    @Test
    void validateTrainingType_valid() {
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
    void validateTrainingType_null_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateTrainingType(null));
    }

    @Test
    void validateTrainingType_invalidId_throws() {
        TrainingType type = new TrainingType();
        type.setId(0L);
        type.setTrainingTypeName("Boxing");

        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateTrainingType(type));
    }

    @Test
    void validateTrainingType_missingName_throws() {
        TrainingType type = new TrainingType();
        type.setId(5L);
        type.setTrainingTypeName("");

        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validateTrainingType(type));
    }

    @Test
    void validateTrainingType_nameMismatch_throws() {
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

    // ---------------------------------------------------------
    // validate(User)
    // ---------------------------------------------------------

    @Test
    void validateUser_success() {
        User user = new User();
        user.setUsername("john");
        user.setFirstName("John");
        user.setLastName("Doe");

        when(userDao.findByUsername("john")).thenReturn(Optional.of(new User()));

        assertDoesNotThrow(() -> validatorService.validate(user));
    }

    @Test
    void validateUser_usernameNotFound_throws() {
        User user = new User();
        user.setUsername("ghost");
        user.setFirstName("A");
        user.setLastName("B");

        when(userDao.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> validatorService.validate(user));
    }
}
