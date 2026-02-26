package unit;

import org.example.dao.*;
import org.example.entities.*;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTraining_success() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");

        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");

        TrainingType type = new TrainingType();
        LocalDate date = LocalDate.now();

        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainerService.selectTrainerByUsername("trainer1")).thenReturn(trainer);

        Training result = trainingService.addTraining(
                "trainee1",
                "trainer1",
                "Yoga",
                type,
                date,
                60L
        );

        assertNotNull(result);
        assertEquals("Yoga", result.getName());
        assertEquals(trainee, result.getTrainee());
        assertEquals(trainer, result.getTrainer());
        assertEquals(type, result.getTrainingType());
        assertEquals(date, result.getDate());
        assertEquals(60L, result.getDuration());

        verify(trainingDao).addTraining(result);
    }

    @Test
    void addTraining_missingTrainee_throwsException() {
        when(traineeDao.findByUsername("missing")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.addTraining(
                        "missing",
                        "trainer1",
                        "Yoga",
                        new TrainingType(),
                        LocalDate.now(),
                        60L
                )
        );

        assertEquals("Trainee not found: missing", ex.getMessage());
    }

    @Test
    void addTraining_missingTrainer_throwsException() {
        when(traineeDao.findByUsername("trainee1"))
                .thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findByUsername("missing"))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.addTraining(
                        "trainee1",
                        "missing",
                        "Yoga",
                        new TrainingType(),
                        LocalDate.now(),
                        60L
                )
        );

        assertEquals("Trainer not found: missing", ex.getMessage());
    }

    @Test
    void addTraining_nullDate_throwsException() {
        when(traineeDao.findByUsername("trainee1"))
                .thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findByUsername("trainer1"))
                .thenReturn(Optional.of(new Trainer()));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.addTraining(
                        "trainee1",
                        "trainer1",
                        "Yoga",
                        new TrainingType(),
                        null,
                        60L
                )
        );

        assertEquals("trainingDate is required", ex.getMessage());
    }

    @Test
    void addTraining_invalidDuration_throwsException() {
        when(traineeDao.findByUsername("trainee1"))
                .thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findByUsername("trainer1"))
                .thenReturn(Optional.of(new Trainer()));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.addTraining(
                        "trainee1",
                        "trainer1",
                        "Yoga",
                        new TrainingType(),
                        LocalDate.now(),
                        -10L
                )
        );

        assertEquals("trainingDuration must be positive", ex.getMessage());
    }

    @Test
    void deleteTraining_success() {
        Training training = new Training();
        when(trainingDao.findTrainingById(1L)).thenReturn(training);

        trainingService.deleteTraining(1L);

        verify(trainingDao).deleteTraining(training);
    }

    @Test
    void findTrainingById_success() {
        Training training = new Training();
        when(trainingDao.findTrainingById(1L)).thenReturn(training);

        Training result = trainingService.findTrainingById(1L);

        assertEquals(training, result);
    }

    @Test
    void findTrainingTypeById_success() {
        TrainingType type = new TrainingType();
        when(trainingTypeDao.findById(1L)).thenReturn(Optional.of(type));

        TrainingType result = trainingService.findTrainingTypeById(1L);

        assertEquals(type, result);
    }

    @Test
    void findTrainingTypeById_invalid_throwsException() {
        when(trainingTypeDao.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.findTrainingTypeById(99L)
        );

        assertEquals("Invalid training type id", ex.getMessage());
    }
}
