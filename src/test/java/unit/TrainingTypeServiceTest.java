package unit;

import org.example.dao.TrainingTypeDao;
import org.example.entities.TrainingType;
import org.example.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingTypeServiceTest {

    private TrainingTypeDao trainingTypeDao;
    private TrainingTypeService trainingTypeService;

    @BeforeEach
    void setup() {
        trainingTypeDao = mock(TrainingTypeDao.class);
        trainingTypeService = new TrainingTypeService(trainingTypeDao);
    }

    @Test
    void getAllTrainingTypes_ShouldReturnList() {
        List<TrainingType> list = Arrays.asList(
                new TrainingType(),
                new TrainingType()
        );

        when(trainingTypeDao.findAll()).thenReturn(list);

        List<TrainingType> result = trainingTypeService.getAllTrainingTypes();

        assertEquals(2, result.size());
        verify(trainingTypeDao, times(1)).findAll();
    }

    @Test
    void getTrainingTypeById_ShouldReturnType() {
        TrainingType type = new TrainingType();
        when(trainingTypeDao.findById(1L)).thenReturn(Optional.of(type));

        TrainingType result = trainingTypeService.getTrainingTypeById(1L);

        assertNotNull(result);
        verify(trainingTypeDao, times(1)).findById(1L);
    }

    @Test
    void getTrainingTypeById_NotFound_ShouldThrow() {
        when(trainingTypeDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainingTypeService.getTrainingTypeById(99L)
        );
    }
}
