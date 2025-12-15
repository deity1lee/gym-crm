package trainee;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.service.AuthService;
import org.example.service.TraineeService;
import org.example.service.ValidatorService;
import org.example.utilities.TrainingSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private AuthService authService;
    private ValidatorService validatorService;

    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);
        authService = mock(AuthService.class);
        validatorService = mock(ValidatorService.class);

        traineeService = new TraineeService();
        traineeService.setTraineeDao(traineeDao);
        traineeService.setTrainerDao(trainerDao);
        traineeService.setAuthService(authService);
        traineeService.setValidatorService(validatorService);
    }

    @Test
    void testCreateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        when(traineeDao.countByFullName("John", "Doe")).thenReturn(0L);
        when(trainerDao.countByFullName("John", "Doe")).thenReturn(0L);

        traineeService.createTrainee(trainee);

        verify(validatorService).validate(trainee);
        verify(traineeDao).create(trainee);
    }

    @Test
    void testSelectTraineeByUsername_Success() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");

        when(authService.authenticateUser("admin", "pass")).thenReturn(null);
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.selectTraineeByUsername("admin", "pass", "trainee1");

        assertEquals(trainee, result);
    }

    @Test
    void testSelectTraineeByUsername_NotFound() {
        when(authService.authenticateUser("admin", "pass")).thenReturn(null);
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                traineeService.selectTraineeByUsername("admin", "pass", "trainee1")
        );
    }

    @Test
    void testChangePassword_StrongPassword() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("trainee1");
        trainee.setPassword("oldPass");

        when(authService.authenticateUser("admin", "adminPass")).thenReturn(null);
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(validatorService.isStrongPassword("newStrongPass")).thenReturn(true);

        traineeService.changePassword("admin", "adminPass", "trainee1", "oldPass", "newStrongPass");

        verify(traineeDao).updatePassword(1L, "newStrongPass");
    }

    @Test
    void testChangePassword_WeakPassword_NoUpdate() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("trainee1");
        trainee.setPassword("oldPass");

        when(authService.authenticateUser("admin", "adminPass")).thenReturn(null);
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(validatorService.isStrongPassword("weak")).thenReturn(false);

        traineeService.changePassword("admin", "adminPass", "trainee1", "oldPass", "weak");

        verify(traineeDao, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void testUpdateTrainee() {
        Trainee newData = new Trainee();
        newData.setUsername("trainee1");
        newData.setFirstName("New");
        newData.setLastName("Name");
        newData.setAddress("New Addr");

        Trainee existing = new Trainee();
        existing.setUsername("trainee1");

        when(authService.authenticateUser("admin", "adminPass")).thenReturn(null);
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(existing));

        traineeService.updateTrainee("admin", "adminPass", newData);

        verify(validatorService).validate(newData);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeDao).updateProfile(captor.capture());

        Trainee updated = captor.getValue();
        assertEquals("New", updated.getFirstName());
        assertEquals("Name", updated.getLastName());
        assertEquals("New Addr", updated.getAddress());
    }

    @Test
    void testActivateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");

        when(authService.authenticateUser("admin", "adminPass")).thenReturn(null);
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        traineeService.activateTrainee("admin", "adminPass", "trainee1");

        verify(traineeDao).activateTrainee(trainee);
    }

    @Test
    void testDeactivateTrainee() {
        Trainee trainee = new Trainee();

        when(authService.authenticateUser("admin", "adminPass")).thenReturn(null);
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        traineeService.deActivateTrainee("admin", "adminPass", "trainee1");

        verify(traineeDao).deactivateTrainee(trainee);
    }

    @Test
    void testDeleteTrainee() {
        Trainee trainee = new Trainee();

        when(authService.authenticateUser("admin", "adminPass")).thenReturn(null);
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        traineeService.deleteTraineeByUsername("admin", "adminPass", "trainee1");

        verify(traineeDao).deleteTrainee(trainee);
    }

    @Test
    void testGetTraineeTrainings() {
        TrainingSearchCriteria criteria = new TrainingSearchCriteria();
        Training training = new Training();
        training.setName("Sprint");

        when(authService.authenticateUser("admin", "adminPass")).thenReturn(null);
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(new Trainee()));
        when(traineeDao.findByTraineeUsernameAndCriteria("trainee1", criteria))
                .thenReturn(List.of(training));

        List<Training> result = traineeService.getTraineeTrainings("admin", "adminPass", "trainee1", criteria);

        assertEquals(1, result.size());
        assertEquals("Sprint", result.get(0).getName());
    }

    @Test
    void testGetUnassignedTrainers() {
        Trainer trainer = new Trainer();
        trainer.setUsername("t1");

        when(authService.authenticateUser("admin", "adminPass")).thenReturn(null);
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(new Trainee()));
        when(traineeDao.findUnassignedTrainers("trainee1")).thenReturn(List.of(trainer));

        List<Trainer> result = traineeService.getUnassignedTrainers("admin", "adminPass", "trainee1");

        assertEquals(1, result.size());
        assertEquals("t1", result.get(0).getUsername());
    }

    @Test
    void testUpdateTraineeTrainers() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");

        Trainer inputTrainer = new Trainer();
        inputTrainer.setUsername("trainer1");

        Trainer fetchedTrainer = new Trainer();
        fetchedTrainer.setUsername("trainer1");

        when(authService.authenticateUser("admin", "adminPass")).thenReturn(null);
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(fetchedTrainer));

        traineeService.updateTraineeTrainers("admin", "adminPass", "trainee1", List.of(inputTrainer));

        verify(traineeDao).updateProfile(trainee);
        assertEquals(1, trainee.getTrainers().size());
    }
}
