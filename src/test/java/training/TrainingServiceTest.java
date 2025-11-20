package training;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.dao.TrainingDao;
import org.example.dao.TrainingTypeDao;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.example.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TrainingServiceTest {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingTypeDao trainingTypeDao;
    private TrainingDao trainingDao;

    private TrainingService trainingService;

    @BeforeEach
    void setup() {
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);
        trainingTypeDao = mock(TrainingTypeDao.class);
        trainingDao = mock(TrainingDao.class);

        trainingService = new TrainingService(traineeDao, trainerDao, trainingTypeDao, trainingDao);
    }

    @Test
    void addTraining_Success() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType type = new TrainingType();

        when(traineeDao.findByUsername("t1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("tr1")).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByName("Yoga")).thenReturn(Optional.of(type));

        trainingService.addTraining("t1", "tr1", "Yoga", new Date(), 30L, "Morning");

        verify(trainingDao).addTraining(any(Training.class));
    }

    @Test
    void addTraining_NoTrainee_ShouldThrow() {
        when(traineeDao.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining("missing", "tr1", "Yoga", new Date(), 10L, "X"));
    }

    @Test
    void addTraining_NoTrainer_ShouldThrow() {
        when(traineeDao.findByUsername("t1")).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining("t1", "missing", "Yoga", new Date(), 10L, "X"));
    }

    @Test
    void addTraining_NoTrainingType_ShouldThrow() {
        when(traineeDao.findByUsername("t1")).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findByUsername("t2")).thenReturn(Optional.of(new Trainer()));
        when(trainingTypeDao.findByName("BadType")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining("t1", "t2", "BadType", new Date(), 10L, "X"));
    }

    @Test
    void addTraining_InvalidDuration_ShouldThrow() {
        when(traineeDao.findByUsername("t1")).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findByUsername("t2")).thenReturn(Optional.of(new Trainer()));
        when(trainingTypeDao.findByName("Yoga")).thenReturn(Optional.of(new TrainingType()));

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining("t1", "t2", "Yoga", new Date(), -5L, "X"));
    }
}
