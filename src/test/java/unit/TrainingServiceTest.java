package unit;


import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.dao.TrainingDao;
import org.example.dao.TrainingTypeDao;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingDao trainingDao;
    private TrainingTypeDao trainingTypeDao;

    private TraineeService traineeService;
    private TrainerService trainerService;

    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);
        trainingDao = mock(TrainingDao.class);
        trainingTypeDao = mock(TrainingTypeDao.class);

        traineeService = mock(TraineeService.class);
        trainerService = mock(TrainerService.class);

        trainingService = new TrainingService(
                traineeService,
                trainerService,
                trainingDao,
                trainingTypeDao
        );

        // inject DAOs via setters
        trainingService.setTraineeDao(traineeDao);
        trainingService.setTrainerDao(trainerDao);
    }

    @Test
    void testAddTraining_Success() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType type = new TrainingType();
        Date date = new Date();

        when(traineeDao.findByUsername("t1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("tr1")).thenReturn(Optional.of(trainer));

        when(traineeService.selectTraineeByUsername("t1")).thenReturn(trainee);
        when(trainerService.selectTrainerByUsername("tr1")).thenReturn(trainer);

        trainingService.addTraining("t1", "tr1", "SomeTraining", type, date, 30L);

        verify(trainingDao).addTraining(any(Training.class));
    }

    @Test
    void testAddTraining_TraineeNotFound() {
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining(
                        "missing",
                        "tr1",
                        "Name",
                        new TrainingType(),
                        new Date(),
                        10L
                )
        );
    }

    @Test
    void testAddTraining_TrainerNotFound() {
        Trainee trainee = new Trainee();

        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining(
                        "t1",
                        "missing",
                        "Name",
                        new TrainingType(),
                        new Date(),
                        10L
                )
        );
    }

    @Test
    void addTraining_InvalidDuration_ShouldThrow() {
        when(traineeDao.findByUsername("t1")).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findByUsername("t2")).thenReturn(Optional.of(new Trainer()));

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining(
                        "t1",
                        "t2",
                        "Name",
                        new TrainingType(),
                        new Date(),
                        -5L
                )
        );
    }

    @Test
    void addTraining_NoDate_ShouldThrow() {
        when(traineeDao.findByUsername("t1")).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findByUsername("t2")).thenReturn(Optional.of(new Trainer()));

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining(
                        "t1",
                        "t2",
                        "Training",
                        new TrainingType(),
                        null,
                        10L
                )
        );
    }
}
