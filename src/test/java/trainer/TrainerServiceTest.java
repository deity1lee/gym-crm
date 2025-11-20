package trainer;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.entities.*;
import org.example.service.AuthService;
import org.example.service.TrainerService;
import org.example.service.ValidatorService;
import org.example.utilities.TrainingSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TrainerServiceTest {

    private TrainerDao trainerDao;
    private TraineeDao traineeDao;
    private AuthService authService;
    private ValidatorService validatorService;

    private TrainerService trainerService;

    @BeforeEach
    void setup() {
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
    void createTrainer_ShouldValidate_AndCallDao() {
        Trainer trainer = new Trainer("John", "Doe", new TrainingType("fitness"));

        when(traineeDao.countByFullName("John", "Doe")).thenReturn(0L);
        when(trainerDao.countByFullName("John", "Doe")).thenReturn(0L);

        trainerService.createTrainer(trainer);

        verify(validatorService).validateFirstName("John");
        verify(validatorService).validateLastName("Doe");
        verify(trainerDao).create(any(Trainer.class));
    }

    @Test
    void selectByUsername_ReturnsTrainer() {
        Trainer found = new Trainer();
        found.setUsername("X");

        when(authService.authenticateTrainer("john", "pass")).thenReturn(Optional.of(new Trainer()));
        when(trainerDao.findByUsername("X")).thenReturn(Optional.of(found));

        Trainer result = trainerService.selectByUsername("john", "pass", "X");

        assertEquals("X", result.getUsername());
    }

    @Test
    void selectByUsername_NotAuthenticated() {
        when(authService.authenticateTrainer("john", "wrong")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainerService.selectByUsername("john", "wrong", "X"));
    }

    @Test
    void changePassword_Strong_ShouldUpdate() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setPassword("old");

        when(authService.authenticateTrainer("john", "old")).thenReturn(Optional.of(trainer));
        when(validatorService.isStrongPassword("NEW")).thenReturn(true);

        trainerService.changePassword("john", "old", "NEW");

        verify(trainerDao).updatePassword(1L, "NEW");
    }

    @Test
    void changePassword_Weak_ShouldNotUpdate() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);

        when(authService.authenticateTrainer("john", "old")).thenReturn(Optional.of(trainer));
        when(validatorService.isStrongPassword("weak")).thenReturn(false);

        trainerService.changePassword("john", "old", "weak");

        verify(trainerDao, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void updateTrainer_ShouldCallDao() {
        Trainer existing = new Trainer();
        Trainer updated = new Trainer("A", "B", new TrainingType("x"));

        when(authService.authenticateTrainer("john", "pass")).thenReturn(Optional.of(existing));

        trainerService.updateTrainer("john", "pass", updated);

        assertEquals("A", existing.getFirstName());
        assertEquals("B", existing.getLastName());
        verify(trainerDao).updateProfile(existing);
    }

    @Test
    void activateTrainer_CallsDao() {
        Trainer tr = new Trainer();

        when(authService.authenticateTrainer("john", "pass")).thenReturn(Optional.of(tr));

        trainerService.activateTrainer("john", "pass");

        verify(trainerDao).activateTrainer(tr);
    }

    @Test
    void deActivateTrainer_CallsDao() {
        Trainer tr = new Trainer();

        when(authService.authenticateTrainer("john", "pass")).thenReturn(Optional.of(tr));

        trainerService.deActivateTrainer("john", "pass");

        verify(trainerDao).deActivateTrainer(tr);
    }

    @Test
    void getTrainerTrainings_ShouldReturnList() {
        Trainer tr = new Trainer();

        when(authService.authenticateTrainer("john", "pass")).thenReturn(Optional.of(tr));

        Training training = new Training();
        List<Training> list = List.of(training);

        TrainingSearchCriteria crit = new TrainingSearchCriteria();

        when(trainerDao.findByTrainerUsernameAndCriteria("john", crit)).thenReturn(list);

        List<Training> result = trainerService.getTrainerTrainings("john", "pass", crit);

        assertEquals(1, result.size());
    }
}
