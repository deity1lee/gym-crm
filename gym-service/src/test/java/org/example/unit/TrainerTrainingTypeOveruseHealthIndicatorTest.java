package org.example.unit;

import org.example.actuator.health.TrainerTrainingTypeOveruseHealthIndicator;
import org.example.dao.TrainingDao;
import org.example.dto.TrainingTypeCountPerTrainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.boot.actuate.health.Health;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerTrainingTypeOveruseHealthIndicatorTest {

    @Mock
    private TrainingDao trainingDao;

    private TrainerTrainingTypeOveruseHealthIndicator indicator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        indicator = new TrainerTrainingTypeOveruseHealthIndicator(trainingDao);
    }

    @Test
    void healthUpWhenAllTrainersWithinLimit() {
        TrainingTypeCountPerTrainer row = new TrainingTypeCountPerTrainer("alice", 2);

        when(trainingDao.findTrainingTypesPerTrainer())
                .thenReturn(List.of(row));

        Health health = indicator.health();

        assertEquals("UP", health.getStatus().getCode());
        assertEquals("All trainers teach within allowed training types",
                health.getDetails().get("message"));
    }

    @Test
    void healthDownWhenTrainerExceedsMaxTypes() {
        TrainingTypeCountPerTrainer row = new TrainingTypeCountPerTrainer("bob", 4);

        when(trainingDao.findTrainingTypesPerTrainer())
                .thenReturn(List.of(row));

        Health health = indicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        assertEquals("bob", health.getDetails().get("trainer"));

        Number types = (Number) health.getDetails().get("distinctTrainingTypes");
        assertEquals(4, types.intValue());

        Number max = (Number) health.getDetails().get("limit");
        assertEquals(2, max.intValue());

        assertEquals("Trainer is teaching too many training types",
                health.getDetails().get("reason"));
    }
}
