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
        Training training = new Training(
                100L, 1L, 2L, "Morning Yoga",
                TrainingType.valueOf("YOGA"), "2025-11-05", 60L);
        trainingService.createTraining(training);

        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingDao).save(captor.capture());
        assertEquals(100L, captor.getValue().getId());
    }

    @Test
    void testGetTrainingByIdFound() {
        Training training = new Training(
                101L, 1L, 2L, "Cardio",
                TrainingType.valueOf("CARDIO"), "2025-11-05", 45L);

        when(trainingDao.findById(101L)).thenReturn(Optional.of(training));

        Optional<Training> result = trainingService.getTrainingById(101L);

        assertTrue(result.isPresent());
        assertEquals(101L, result.get().getId());
        verify(trainingDao).findById(101L);
    }

    @Test
    void testGetTrainingByNameNotFound() {
        when(trainingDao.findById(999L)).thenReturn(Optional.empty());

        Optional<Training> result = trainingService.getTrainingById(999L);

        assertFalse(result.isPresent());
        verify(trainingDao).findById(999L);
    }

    @Test
    void testGetAllTrainings() {
        List<Training> trainings = List.of(
                new Training(200L, 1L, 2L, "Yoga", TrainingType.valueOf("YOGA"), "2025-11-05", 60L),
                new Training(201L, 3L, 4L, "Boxing", TrainingType.valueOf("CARDIO"), "2025-11-06", 45L)
        );

        when(trainingDao.findAll()).thenReturn(trainings);

        List<Training> result = trainingService.getAllTrainings();

        assertEquals(2, result.size());
        assertEquals(200L, result.get(0).getId());
        verify(trainingDao).findAll();
    }
}
