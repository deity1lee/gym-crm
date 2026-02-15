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
        assertEquals("All active trainers have at least one trainee",
                health.getDetails().get("message"));
    }

    @Test
    void healthDownWhenSomeTrainersUnassigned() {
        when(trainerDao.countActiveTrainersWithoutTrainees())
                .thenReturn(3L);

        Health health = indicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        Number unassigned = (Number) health.getDetails().get("unassignedTrainersCount");
        assertEquals(3, unassigned.intValue());

        assertEquals("There are trainers without trainees",
                health.getDetails().get("problem"));
    }
}
