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

import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private AuthService authService;
    private ValidatorService validatorService;

    private TraineeService traineeService;

    @BeforeEach
    void setup() {
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
    void createTrainee_ShouldValidateNames_AndCallDaoCreate() {
        Trainee trainee = new Trainee("John", "Doe", Date.valueOf( "2000-01-01"), "NY");

        when(traineeDao.countByFullName("John", "Doe")).thenReturn(0L);
        when(trainerDao.countByFullName("John", "Doe")).thenReturn(0L);

        traineeService.createTrainee(trainee);

        verify(validatorService).validate(trainee);
        verify(traineeDao).create(any(Trainee.class));
    }

    @Test
    void selectByUsername_ShouldAuthenticate_AndReturnTrainee() {
        Trainee trainingUser = new Trainee();
        trainingUser.setUsername("TargetUser");

        when(authService.authenticateUser("john", "pass"))
                .thenReturn(Optional.of(new Trainee()));

        when(traineeDao.findByUsername("TargetUser"))
                .thenReturn(Optional.of(trainingUser));

        Trainee found = traineeService.selectTraineeByUsername(
                "john", "pass", "TargetUser"
        );

        assertEquals("TargetUser", found.getUsername());
    }

    @Test
    void selectByUsername_Throws_WhenNotAuthenticated() {
        when(authService.authenticateTrainee("john", "wrong"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                traineeService.selectTraineeByUsername("john", "wrong", "TargetUser")
        );
    }

    @Test
    void selectByUsername_Throws_WhenTraineeNotFound() {
        when(authService.authenticateTrainee("john", "pass"))
                .thenReturn(Optional.of(new Trainee()));

        when(traineeDao.findByUsername("TargetUser"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                traineeService.selectTraineeByUsername("john", "pass", "TargetUser")
        );
    }

    @Test
    void changePassword_ShouldUpdatePassword_WhenStrong() {
        Trainee trainee = new Trainee();
        trainee.setId(5L);
        trainee.setPassword("old");

        when(authService.authenticateTrainee("john", "old"))
                .thenReturn(Optional.of(trainee));

        when(validatorService.isStrongPassword("NEW_STRONG"))
                .thenReturn(true);

        traineeService.changePassword("john", "old", "NEW_STRONG");

        verify(traineeDao).updatePassword(5L, "NEW_STRONG");
    }

    @Test
    void changePassword_ShouldNotUpdate_WhenWeak() {
        Trainee trainee = new Trainee();
        trainee.setId(5L);

        when(authService.authenticateTrainee("john", "old"))
                .thenReturn(Optional.of(trainee));

        when(validatorService.isStrongPassword("weak"))
                .thenReturn(false);

        traineeService.changePassword("john", "old", "weak");

        verify(traineeDao, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void updateTrainee_ShouldSetFields_AndCallDao() {
        Trainee existing = new Trainee();
        existing.setFirstName("Old");
        existing.setLastName("Name");
        existing.setAddress("Old Address");

        Trainee updated = new Trainee("New", "Name2", Date.valueOf("2000-01-09"), "New Address");

        when(authService.authenticateTrainee("john", "pass"))
                .thenReturn(Optional.of(existing));

        traineeService.updateTrainee("john", "pass", updated);

        assertEquals("New", existing.getFirstName());
        assertEquals("Name2", existing.getLastName());
        assertEquals("New Address", existing.getAddress());

        verify(traineeDao).updateProfile(existing);
    }

    @Test
    void activateTrainee_ShouldAuthenticateAndCallDao() {
        Trainee t = new Trainee();
        when(authService.authenticateTrainee("john", "pass"))
                .thenReturn(Optional.of(t));

        traineeService.activateTrainee("john", "pass");

        verify(traineeDao).activateTrainee(t);
    }

    @Test
    void deActivateTrainee_ShouldAuthenticateAndCallDao() {
        Trainee t = new Trainee();
        when(authService.authenticateTrainee("john", "pass"))
                .thenReturn(Optional.of(t));

        traineeService.deActivateTrainee("john", "pass");

        verify(traineeDao).deActivateTrainee(t);
    }

    @Test
    void deleteTrainee_ShouldAuthenticate_AndCallDao() {
        Trainee t = new Trainee();
        when(authService.authenticateTrainee("john", "pass"))
                .thenReturn(Optional.of(t));

        traineeService.deleteTraineeByUsername("john", "pass");

        verify(traineeDao).deleteTrainee(t);
    }

    @Test
    void getTraineeTrainings_ShouldReturnFromDao() {
        when(authService.authenticateTrainee("john", "pass"))
                .thenReturn(Optional.of(new Trainee()));

        Training tr = new Training();
        List<Training> list = List.of(tr);

        TrainingSearchCriteria crit = new TrainingSearchCriteria();

        when(traineeDao.findByTraineeUsernameAndCriteria("john", crit))
                .thenReturn(list);

        List<Training> result =
                traineeService.getTraineeTrainings("john", "pass", crit);

        assertEquals(1, result.size());
    }

    @Test
    void getUnassignedTrainers_ShouldAuthenticate_AndReturn() {
        Trainee trainee = new Trainee();
        when(authService.authenticateTrainee("john", "pass"))
                .thenReturn(Optional.of(trainee));

        Trainer trainer = new Trainer();
        trainer.setUsername("T1");

        when(traineeDao.findUnassignedTrainers("john"))
                .thenReturn(List.of(trainer));

        List<Trainer> result =
                traineeService.getUnassignedTrainers("john", "pass");

        assertEquals(1, result.size());
        verify(traineeDao).findUnassignedTrainers("john");
    }

    @Test
    void updateTraineeTrainers_ShouldReplaceTrainerList_AndUpdateDao() {
        Trainee trainee = new Trainee();
        trainee.setTrainers(new ArrayList<>());

        Trainer trainer = new Trainer();
        trainer.setUsername("t1");

        when(authService.authenticateTrainee("john", "pass"))
                .thenReturn(Optional.of(trainee));

        when(trainerDao.findByUsername("t1"))
                .thenReturn(Optional.of(trainer));

        traineeService.updateTraineeTrainers("john", "pass", List.of(trainer));

        assertEquals(1, trainee.getTrainers().size());
        verify(traineeDao).updateProfile(trainee);
    }
}
