package trainer;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.service.AuthService;
import org.example.service.TrainerService;
import org.example.service.ValidatorService;
import org.example.utilities.TrainingSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    private TrainerDao trainerDao;
    private TraineeDao traineeDao;
    private AuthService authService;
    private ValidatorService validatorService;

    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerDao = mock(TrainerDao.class);
        traineeDao = mock(TraineeDao.class);
        authService = mock(AuthService.class);
        validatorService = mock(ValidatorService.class);

        trainerService = new TrainerService();
        trainerService.setTrainerDao(trainerDao);
        trainerService.setTraineeDao(traineeDao);
        trainerService.setAuthService(authService);
        trainerService.setValidatorService(validatorService);
    }

    @Test
    void testCreateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Doe");

        when(traineeDao.countByFullName("John", "Doe")).thenReturn(0L);
        when(trainerDao.countByFullName("John", "Doe")).thenReturn(0L);

        trainerService.createTrainer(trainer);

        verify(validatorService).validate(trainer);
        verify(trainerDao).create(trainer);
    }

    @Test
    void testSelectTrainerByUsername_Success() {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");

        when(authService.authenticateUser("admin", "pass")).thenReturn(null);
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.selectTrainerByUsername("admin", "pass", "trainer1");

        assertEquals(trainer, result);
    }

    @Test
    void testSelectTrainerByUsername_NotFound() {
        when(authService.authenticateUser("admin", "pass")).thenReturn(null);
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainerService.selectTrainerByUsername("admin", "pass", "trainer1"));
    }

    @Test
    void testChangePassword_StrongPassword() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("trainer1");
        trainer.setPassword("oldPass");

        when(authService.authenticateUser("admin", "adminPass")).thenReturn(null);
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(validatorService.isStrongPassword("newPass")).thenReturn(true);

        trainerService.changePassword("admin", "adminPass", "trainer1", "oldPass", "newPass");

        verify(trainerDao).updatePassword(1L, "newPass");
    }

    @Test
    void testChangePassword_WeakPassword() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("trainer1");
        trainer.setPassword("oldPass");

        when(authService.authenticateUser("admin", "adminPass")).thenReturn(null);
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(validatorService.isStrongPassword("weak")).thenReturn(false);

        trainerService.changePassword("admin", "adminPass", "trainer1", "oldPass", "weak");

        verify(trainerDao, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void testUpdateTrainer() {
        Trainer newTrainer = new Trainer();
        newTrainer.setUsername("trainer1");
        newTrainer.setFirstName("New");
        newTrainer.setLastName("Name");

        Trainer existing = new Trainer();
        existing.setUsername("trainer1");

        when(authService.authenticateUser("admin", "adminPass")).thenReturn(null);
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(existing));

        trainerService.updateTrainer("admin", "adminPass", newTrainer);

        verify(validatorService).validate(newTrainer);
        verify(trainerDao).updateProfile(newTrainer);
    }

    @Test
    void testActivateTrainer() {
        Trainer trainer = new Trainer();

        when(authService.authenticateUser("admin", "adminPass")).thenReturn(null);
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        trainerService.activateTrainer("admin", "adminPass", "trainer1");

        verify(trainerDao).activateTrainer(trainer);
    }

    @Test
    void testDeactivateTrainer() {
        Trainer trainer = new Trainer();

        when(authService.authenticateUser("admin", "adminPass")).thenReturn(null);
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        trainerService.deActivateTrainer("admin", "adminPass", "trainer1");

        verify(trainerDao).deactivateTrainer(trainer);
    }

    @Test
    void testGetTrainerTrainings() {
        TrainingSearchCriteria criteria = new TrainingSearchCriteria();
        Training training = new Training();
        training.setName("Sprint");

        when(authService.authenticateUser("admin", "adminPass")).thenReturn(null);
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(new Trainer()));
        when(traineeDao.findByTraineeUsernameAndCriteria("trainer1", criteria))
                .thenReturn(List.of(training));

        List<Training> result = trainerService.getTrainerTrainings("admin", "adminPass", "trainer1", criteria);

        assertEquals(1, result.size());
        assertEquals("Sprint", result.get(0).getName());
    }
}
