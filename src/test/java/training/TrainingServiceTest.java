package org.example.service;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.dao.TrainingDao;
import org.example.dao.TrainingTypeDao;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingTypeDao trainingTypeDao;
    private TrainingDao trainingDao;

    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);
        trainingTypeDao = mock(TrainingTypeDao.class);
        trainingDao = mock(TrainingDao.class);

        trainingService = new TrainingService(traineeDao, trainerDao, trainingTypeDao, trainingDao);
    }

    @Test
    void testAddTraining_Success() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType type = new TrainingType();

        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByName("yoga")).thenReturn(Optional.of(type));

        trainingService.addTraining(
                "trainee1",
                "trainer1",
                "yoga",
                new Date(),
                60L,
                "Morning Yoga"
        );

        verify(trainingDao).addTraining(any(Training.class));
    }

    @Test
    void testAddTraining_TraineeNotFound() {
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining(
                        "trainee1",
                        "trainer1",
                        "yoga",
                        new Date(),
                        60L,
                        "Morning Yoga"
                ));
    }

    @Test
    void testAddTraining_TrainerNotFound() {
        Trainee trainee = new Trainee();

        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining(
                        "trainee1",
                        "trainer1",
                        "yoga",
                        new Date(),
                        60L,
                        "Morning Yoga"
                ));
    }

    @Test
    void testAddTraining_TrainingTypeNotFound() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();

        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByName("yoga")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining(
                        "trainee1",
                        "trainer1",
                        "yoga",
                        new Date(),
                        60L,
                        "Morning Yoga"
                ));
    }

    @Test
    void testAddTraining_InvalidDuration() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType type = new TrainingType();

        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByName("yoga")).thenReturn(Optional.of(type));

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining(
                        "trainee1",
                        "trainer1",
                        "yoga",
                        new Date(),
                        0L,
                        "Morning Yoga"
                ));
    }
}
