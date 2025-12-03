package unit;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.dao.TrainingTypeDao;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.service.AuthService;
import org.example.service.TraineeService;
import org.example.service.ValidatorService;
import org.example.utilities.TrainingSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.MDC;

import java.util.*;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private AuthService authService;

    @Mock
    private ValidatorService validatorService;

    @InjectMocks
    private TraineeService traineeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        MDC.put("transactionId", "test-tx");
    }

    @Test
    void createTrainee_success() {
        Trainee trainee = new Trainee();

        doNothing().when(validatorService).validate(trainee);
        doNothing().when(authService).assignCredentials(trainee);

        traineeService.createTrainee(trainee);

        verify(validatorService).validate(trainee);
        verify(authService).assignCredentials(trainee);
        verify(traineeDao).create(trainee);
    }

    @Test
    void selectTraineeByUsername_found() {
        Trainee trainee = new Trainee();
        trainee.setTrainers(new ArrayList<>()); // <-- FIX

        when(traineeDao.findByUsername("john")).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.selectTraineeByUsername("john");

        assertNotNull(result);
    }

    @Test
    void selectTraineeByUsername_notFound() {
        when(traineeDao.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.selectTraineeByUsername("ghost"));
    }

    @Test
    void updateTrainee_success() {
        Trainee existing = new Trainee();
        existing.setUsername("john");
        existing.setTrainers(new ArrayList<>()); // <-- FIX

        Trainee incoming = new Trainee();
        incoming.setUsername("john");
        incoming.setFirstName("A");
        incoming.setLastName("B");

        when(traineeDao.findByUsername("john")).thenReturn(Optional.of(existing));

        Trainee updated = traineeService.updateTrainee(incoming);

        assertEquals("A", updated.getFirstName());
        assertEquals("B", updated.getLastName());
        verify(traineeDao).updateProfile(existing);
    }

    @Test
    void updateActivationStatus_success() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john");

        when(traineeDao.findByUsername("john")).thenReturn(Optional.of(trainee));

        traineeService.updateActivationStatus("john", true);

        assertTrue(trainee.isActive());
        verify(traineeDao).updateProfile(trainee);
    }

    @Test
    void updateActivationStatus_notFound() {
        when(traineeDao.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.updateActivationStatus("ghost", true));
    }

    // -----------------------------------------------------
    // getUnassignedTrainers()
    // -----------------------------------------------------

    @Test
    void getUnassignedTrainers_success() {
        Trainee trainee = new Trainee();
        when(traineeDao.findByUsername("john")).thenReturn(Optional.of(trainee));

        List<Trainer> trainers = List.of(new Trainer(), new Trainer());
        when(traineeDao.findUnassignedTrainers("john")).thenReturn(trainers);

        List<Trainer> result = traineeService.getUnassignedTrainers("john");

        assertEquals(2, result.size());
    }

    @Test
    void getUnassignedTrainers_notFound() {
        when(traineeDao.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.getUnassignedTrainers("ghost"));
    }

    // -----------------------------------------------------
    // updateTraineeTrainers()
    // -----------------------------------------------------

    @Test
    void updateTraineeTrainers_success() {
        Trainee trainee = new Trainee();
        when(traineeDao.findByUsername("john")).thenReturn(Optional.of(trainee));

        Trainer t1 = new Trainer(); t1.setUsername("A");
        Trainer t2 = new Trainer(); t2.setUsername("B");

        when(trainerDao.findByUsername("A")).thenReturn(Optional.of(t1));
        when(trainerDao.findByUsername("B")).thenReturn(Optional.of(t2));

        List<Trainer> result = traineeService.updateTraineeTrainers("john", List.of("A", "B"));

        assertEquals(2, result.size());
        verify(traineeDao).updateProfile(trainee);
    }

    @Test
    void updateTraineeTrainers_trainerNotFound() {
        Trainee trainee = new Trainee();
        when(traineeDao.findByUsername("john")).thenReturn(Optional.of(trainee));

        when(trainerDao.findByUsername("A")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.updateTraineeTrainers("john", List.of("A")));
    }

    @Test
    void getTraineeTrainings_success() {
        Trainee trainee = new Trainee();
        when(traineeDao.findByUsername("john")).thenReturn(Optional.of(trainee));

        TrainingSearchCriteria criteria =
                new TrainingSearchCriteria(null, null, null, "john", null);

        List<Training> trainings = List.of(new Training(), new Training());
        when(traineeDao.findByTraineeUsernameAndCriteria(criteria)).thenReturn(trainings);

        List<Training> result = traineeService.getTraineeTrainings("john", criteria);

        assertEquals(2, result.size());
    }

    @Test
    void getTraineeTrainings_missingUsername() {
        TrainingSearchCriteria criteria =
                new TrainingSearchCriteria(null, null, null, "", null);

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.getTraineeTrainings("john", criteria));
    }

    @Test
    void getTraineeTrainings_invalidDateRange() {
        Date from = new Date(200);
        Date to = new Date(100);

        TrainingSearchCriteria criteria =
                new TrainingSearchCriteria(from, to, null, "john", null);

        when(traineeDao.findByUsername("john")).thenReturn(Optional.of(new Trainee()));

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.getTraineeTrainings("john", criteria));
    }

    @Test
    void getTraineeTrainings_invalidTrainerFilter() {
        TrainingSearchCriteria criteria =
                new TrainingSearchCriteria(null, null, "trainerX", "john", null);

        when(traineeDao.findByUsername("john"))
                .thenReturn(Optional.of(new Trainee()));

        when(trainerDao.findByUsername("trainerX"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.getTraineeTrainings("john", criteria));
    }

    @Test
    void getTraineeTrainings_invalidTrainingType() {
        TrainingSearchCriteria criteria =
                new TrainingSearchCriteria(null, null, null, "john", 99L);

        when(traineeDao.findByUsername("john"))
                .thenReturn(Optional.of(new Trainee()));

        when(trainingTypeDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.getTraineeTrainings("john", criteria));
    }
}
