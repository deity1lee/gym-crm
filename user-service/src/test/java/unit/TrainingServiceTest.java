package unit;

import org.example.dao.*;
import org.example.entities.*;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddTrainingSuccess() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");

        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");

        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainerService.selectTrainerByUsername("trainer1")).thenReturn(trainer);

        TrainingType type = new TrainingType();
        LocalDate date = LocalDate.now();

        Training result = trainingService.addTraining(
                "trainee1",
                "trainer1",
                "Yoga",
                type,
                date,
                60L
        );

        assertNotNull(result);
        assertEquals("trainee1", result.getTrainee().getUsername());
        assertEquals("trainer1", result.getTrainer().getUsername());
        assertEquals("Yoga", result.getName());
        assertEquals(type, result.getTrainingType());
        assertEquals(date, result.getDate());
        assertEquals(60L, result.getDuration());

        verify(trainingDao).addTraining(result);
    }

    @Test
    void testAddTrainingMissingTrainee() {
        when(traineeDao.findByUsername("unknown")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining("unknown", "trainer1", "Yoga", new TrainingType(), LocalDate.now(), 60L)
        );

        assertTrue(ex.getMessage().contains("Trainee not found"));
    }

    @Test
    void testAddTrainingMissingTrainer() {
        Trainee trainee = new Trainee();
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("unknown")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining("trainee1", "unknown", "Yoga", new TrainingType(), LocalDate.now(), 60L)
        );

        assertTrue(ex.getMessage().contains("Trainer not found"));
    }

    @Test
    void testAddTrainingInvalidDate() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining("trainee1", "trainer1", "Yoga", new TrainingType(), null, 60L)
        );

        assertEquals("trainingDate is required", ex.getMessage());
    }

    @Test
    void testAddTrainingInvalidDuration() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining("trainee1", "trainer1", "Yoga", new TrainingType(), LocalDate.now(), -5L)
        );

        assertEquals("trainingDuration must be positive", ex.getMessage());
    }

    @Test
    void testDeleteTraining() {
        Training training = new Training();
        when(trainingDao.findTrainingById(1L)).thenReturn(training);

        trainingService.deleteTraining(1L);

        verify(trainingDao).deleteTraining(training);
    }

    @Test
    void testFindTrainingById() {
        Training training = new Training();
        when(trainingDao.findTrainingById(1L)).thenReturn(training);

        Training result = trainingService.findTrainingById(1L);
        assertEquals(training, result);
    }

    @Test
    void testFindTrainingTypeByIdSuccess() {
        TrainingType type = new TrainingType();
        when(trainingTypeDao.findById(1L)).thenReturn(Optional.of(type));

        TrainingType result = trainingService.findTrainingTypeById(1L);
        assertEquals(type, result);
    }

    @Test
    void testFindTrainingTypeByIdInvalid() {
        when(trainingTypeDao.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                trainingService.findTrainingTypeById(99L)
        );

        assertEquals("Invalid training type id", ex.getMessage());
    }
}
