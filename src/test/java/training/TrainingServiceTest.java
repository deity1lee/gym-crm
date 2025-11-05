package training;

import org.example.dao.TrainingDao;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.example.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainingService = new TrainingService();
        trainingService.setTrainingDao(trainingDao);
    }

    @Test
    void testCreateTraining() {
        Training training = new Training(1L, 2L, "Morning Yoga", new TrainingType("yoga"), "2025-11-05", 60L);

        trainingService.createTraining(training);

        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingDao).save(captor.capture());
        assertEquals("Morning Yoga", captor.getValue().getName());
    }

    @Test
    void testGetTrainingByNameFound() {
        Training training = new Training(1L, 2L, "Cardio", new TrainingType("cardio"), "2025-11-05", 45L);
        when(trainingDao.findByName("Cardio")).thenReturn(Optional.of(training));

        Optional<Training> result = trainingService.getTrainingById("Cardio");

        assertTrue(result.isPresent());
        assertEquals("Cardio", result.get().getName());
        verify(trainingDao).findByName("Cardio");
    }

    @Test
    void testGetTrainingByNameNotFound() {
        when(trainingDao.findByName("Nonexistent")).thenReturn(Optional.empty());

        Optional<Training> result = trainingService.getTrainingById("Nonexistent");

        assertFalse(result.isPresent());
        verify(trainingDao).findByName("Nonexistent");
    }

    @Test
    void testGetAllTrainings() {
        List<Training> trainings = List.of(
                new Training(1L, 2L, "Yoga", new TrainingType("yoga"), "2025-11-05", 60L),
                new Training(3L, 4L, "Boxing", new TrainingType("cardio"), "2025-11-06", 45L)
        );

        when(trainingDao.findAll()).thenReturn(trainings);

        List<Training> result = trainingService.getAllTrainings();

        assertEquals(2, result.size());
        assertEquals("Yoga", result.get(0).getName());
        verify(trainingDao).findAll();
    }
}
