package unit;

import org.example.actuator.health.UnassignedTrainerHealthIndicator;
import org.example.dao.TrainerDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.boot.actuate.health.Health;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnassignedTrainerHealthIndicatorTest {

    @Mock
    private TrainerDao trainerDao;

    private UnassignedTrainerHealthIndicator indicator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        indicator = new UnassignedTrainerHealthIndicator(trainerDao);
    }

    @Test
    void healthUpWhenAllTrainersHaveTrainees() {
        when(trainerDao.countActiveTrainersWithoutTrainees())
                .thenReturn(0L);

        Health health = indicator.health();

        assertEquals("UP", health.getStatus().getCode());
        assertEquals("No more than 20 active trainers do not have trainee",
                health.getDetails().get("message"));
    }

    @Test
    void healthDownWhenMoreThanTwentyTrainersUnassigned() {
        when(trainerDao.countActiveTrainersWithoutTrainees())
                .thenReturn(21L);

        Health health = indicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        Number unassigned = (Number) health.getDetails().get("unassignedTrainersCount");
        assertEquals(21, unassigned.intValue());

        assertEquals("There more than 20 trainers without trainees",
                health.getDetails().get("problem"));
    }
}
