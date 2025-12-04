package trainee;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.entities.Trainee;
import org.example.service.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);

        traineeService = new TraineeService();
        traineeService.setTraineeDao(traineeDao);
        traineeService.setTrainerDao(trainerDao);
    }

    @Test
    void testCreateTraineeGeneratesUsernameAndPassword() {
        Trainee trainee = new Trainee("John", "Smith", "1998-02-15", "New York");

        when(traineeDao.countByFullName("John", "Smith")).thenReturn(0L);

        doAnswer(inv -> {
            Trainee t = inv.getArgument(0);
            t.setId(1L);
            return null;
        }).when(traineeDao).create(any(Trainee.class));

        traineeService.createTrainee(trainee);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeDao).create(captor.capture());

        Trainee saved = captor.getValue();
        assertEquals(1L, saved.getId());
        assertEquals("John.Smith", saved.getUsername());
        assertNotNull(saved.getPassword());
        assertEquals(10, saved.getPassword().length());
        assertTrue(saved.isActive());
    }

    @Test
    void testCreateTraineeAppendsNumberIfDuplicateName() {
        Trainee trainee = new Trainee("John", "Smith", "1999-06-30", "Boston");

        when(traineeDao.getNextId()).thenReturn(2L);
        when(traineeDao.countByFullName("John", "Smith")).thenReturn(1L);
        when(trainerDao.countByFullName("John", "Smith")).thenReturn(0L);


        traineeService.createTrainee(trainee);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeDao).create(captor.capture());

        assertEquals("John.Smith2", captor.getValue().getUsername());
    }

    @Test
    void testUpdateTraineeDelegatesToDao() {
        Trainee trainee = new Trainee(1L, "Alice", "Brown", "2000-05-12", "Chicago");

        traineeService.updateTrainee(trainee);

        verify(traineeDao, times(1)).update(trainee);
    }

    @Test
    void testDeleteTraineeDelegatesToDao() {
        when(traineeDao.findById(1L))
                .thenReturn(Optional.of(new Trainee(1L, "John", "Doe", "john@example.com", "pass123")));
        traineeService.deleteTrainee(1L);

        verify(traineeDao, times(1)).delete(1L);
    }

    @Test
    void testGetTraineeByIdDelegatesToDao() {
        Trainee trainee = new Trainee(1L, "David", "Lee", "2001-10-20", "Miami");
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = traineeService.getTraineeById(1L);

        assertTrue(result.isPresent());
        assertEquals("David", result.get().getFirstName());
        verify(traineeDao, times(1)).findById(1L);
    }

    @Test
    void testGetAllTraineesDelegatesToDao() {
        List<Trainee> trainees = List.of(
                new Trainee(1L, "John", "Smith", "1998-02-15", "NY"),
                new Trainee(2L, "Alice", "Brown", "2000-05-12", "Chicago")
        );
        when(traineeDao.findAll()).thenReturn(trainees);

        List<Trainee> result = traineeService.getAllTrainees();

        assertEquals(2, result.size());
        verify(traineeDao, times(1)).findAll();
    }
}
