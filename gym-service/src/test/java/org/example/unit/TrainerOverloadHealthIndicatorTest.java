package org.example.unit;

import org.example.actuator.health.TrainerOverloadHealthIndicator;
import org.example.dao.TrainerDao;
import org.example.dto.TrainerTraineeCount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.actuate.health.Health;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerOverloadHealthIndicatorTest {

    @Mock
    private TrainerDao trainerDao;

    private TrainerOverloadHealthIndicator indicator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        indicator = new TrainerOverloadHealthIndicator(trainerDao);

        // inject @Value manually
        ReflectionTestUtils.setField(indicator, "maxTrainees", 5);
    }

    @Test
    void healthUpWhenNoTrainerOverloaded() {
        TrainerTraineeCount row = new TrainerTraineeCount("john", 3);

        when(trainerDao.findTrainerTraineeCounts())
                .thenReturn(List.of(row));

        Health health = indicator.health();

        assertEquals("UP", health.getStatus().getCode());
    }

    @Test
    void healthDownWhenTrainerOverloaded() {
        TrainerTraineeCount row = new TrainerTraineeCount("john", 7);

        when(trainerDao.findTrainerTraineeCounts())
                .thenReturn(List.of(row));

        Health health = indicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        assertEquals("john", health.getDetails().get("trainer"));

        // Safe numeric comparison
        Number traineeCount = (Number) health.getDetails().get("traineeCount");
        assertEquals(7, traineeCount.intValue());

        Number limit = (Number) health.getDetails().get("limit");
        assertEquals(5, limit.intValue());
    }
}
