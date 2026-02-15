package unit;

import org.example.dao.TrainingTypeDao;
import org.example.entities.TrainingType;
import org.example.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @InjectMocks
    private TrainingTypeService trainingTypeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTrainingTypesReturnsList() {
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
    void testGetTrainingTypeByIdReturnsType() {
        TrainingType type = new TrainingType();
        when(trainingTypeDao.findById(1L)).thenReturn(Optional.of(type));

        TrainingType result = trainingTypeService.getTrainingTypeById(1L);

        assertNotNull(result);
        verify(trainingTypeDao, times(1)).findById(1L);
    }

    @Test
    void testGetTrainingTypeByIdNotFoundThrows() {
        when(trainingTypeDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainingTypeService.getTrainingTypeById(99L));
    }
}
