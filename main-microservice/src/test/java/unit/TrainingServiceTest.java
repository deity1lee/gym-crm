package unit;

import org.example.dao.*;
import org.example.entities.*;
import org.example.service.TraineeService;
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
    private TraineeDao traineeDao;
    @Mock
    private TrainerDao trainerDao;
    @Mock
    private TrainingDao trainingDao;
    @Mock
    private TrainingTypeDao trainingTypeDao;
    @Mock
    private OutboxDao outboxDao;

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTraining_success() {
        Trainee trainee = new Trainee();
        trainee.setUsername("t1");

        Trainer trainer = new Trainer();
        trainer.setUsername("tr1");

        TrainingType type = new TrainingType();
        LocalDate date = LocalDate.of(2026, 2, 5);

        when(traineeDao.findByUsername("t1"))
                .thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("tr1"))
                .thenReturn(Optional.of(trainer));

        when(traineeService.selectTraineeByUsername("t1"))
                .thenReturn(trainee);
        when(trainerService.selectTrainerByUsername("tr1"))
                .thenReturn(trainer);

        trainingService.addTraining(
                "t1",
                "tr1",
                "SomeTraining",
                type,
                date,
                30L
        );

        ArgumentCaptor<Training> trainingCaptor =
                ArgumentCaptor.forClass(Training.class);

        verify(trainingDao).addTraining(trainingCaptor.capture());
        verify(outboxDao).createPendingRequest(any());

        Training saved = trainingCaptor.getValue();

        assertEquals("SomeTraining", saved.getName());
        assertEquals(trainee, saved.getTrainee());
        assertEquals(trainer, saved.getTrainer());
        assertEquals(type, saved.getTrainingType());
        assertEquals(date, saved.getDate());
        assertEquals(30L, saved.getDuration());
    }

    @Test
    void addTraining_traineeNotFound_throws() {
        when(traineeDao.findByUsername("missing"))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.addTraining(
                        "missing",
                        "tr1",
                        "Name",
                        new TrainingType(),
                        LocalDate.now(),
                        10L
                )
        );

        assertTrue(ex.getMessage().contains("Trainee not found"));

        verify(trainingDao, never()).addTraining(any());
        verify(outboxDao, never()).createPendingRequest(any());
    }

    @Test
    void addTraining_trainerNotFound_throws() {
        Trainee trainee = new Trainee();

        when(traineeDao.findByUsername("t1"))
                .thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("missing"))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.addTraining(
                        "t1",
                        "missing",
                        "Name",
                        new TrainingType(),
                        LocalDate.now(),
                        10L
                )
        );

        assertTrue(ex.getMessage().contains("Trainer not found"));

        verify(trainingDao, never()).addTraining(any());
        verify(outboxDao, never()).createPendingRequest(any());
    }

    @Test
    void addTraining_invalidDuration_throws() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();

        when(traineeDao.findByUsername("t1"))
                .thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("tr1"))
                .thenReturn(Optional.of(trainer));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.addTraining(
                        "t1",
                        "tr1",
                        "Name",
                        new TrainingType(),
                        LocalDate.now(),
                        -5L
                )
        );

        assertTrue(ex.getMessage().contains("trainingDuration"));

        verify(trainingDao, never()).addTraining(any());
        verify(outboxDao, never()).createPendingRequest(any());
    }

    @Test
    void addTraining_missingDate_throws() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();

        when(traineeDao.findByUsername("t1"))
                .thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("tr1"))
                .thenReturn(Optional.of(trainer));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.addTraining(
                        "t1",
                        "tr1",
                        "Training",
                        new TrainingType(),
                        null,
                        10L
                )
        );

        assertTrue(ex.getMessage().contains("trainingDate"));

        verify(trainingDao, never()).addTraining(any());
        verify(outboxDao, never()).createPendingRequest(any());
    }
}
