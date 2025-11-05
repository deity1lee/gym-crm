package trainer;

import org.example.dao.TrainerDao;
import org.example.entities.Trainer;
import org.example.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    private TrainerDao trainerDao;
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerDao = mock(TrainerDao.class);
        trainerService = new TrainerService();
        trainerService.setTrainerDao(trainerDao);
    }

    @Test
    void testCreatetrainerGeneratesUsernameAndPassword() {
        Trainer trainer = new Trainer(1L, "John", "Smith", "nutrition coaching");

        when(trainerDao.countByFullName("John", "Smith")).thenReturn(0L);

        trainerService.createTrainer(trainer);

        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerDao).save(captor.capture());

        Trainer saved = captor.getValue();
        assertEquals("John.Smith", saved.getUsername());
        assertNotNull(saved.getPassword());
        assertEquals(10, saved.getPassword().length());
        assertTrue(saved.isActive());
    }

    @Test
    void testCreatetrainerAppendsNumberIfDuplicateName() {
        Trainer trainer = new Trainer(2L, "John", "Smith", "nutrition coaching");

        when(trainerDao.countByFullName("John", "Smith")).thenReturn(1L);

        trainerService.createTrainer(trainer);

        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerDao).save(captor.capture());

        assertEquals("John.Smith2", captor.getValue().getUsername());
    }

    @Test
    void testUpdatetrainerDelegatesToDao() {
        Trainer trainer = new Trainer(1L, "Alice", "Brown", "nutrition coaching");

        trainerService.updateTrainer(trainer);

        verify(trainerDao, times(1)).save(trainer);
    }

    @Test
    void testGettrainerByIdDelegatesToDao() {
        Trainer trainer = new Trainer(1L, "David", "Lee", "nutrition coaching");
        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = trainerService.getTrainerById(1L);

        assertTrue(result.isPresent());
        assertEquals("David", result.get().getFirstName());
        verify(trainerDao, times(1)).findById(1L);
    }

    @Test
    void testGetAlltrainersDelegatesToDao() {
        List<Trainer> trainers = List.of(
                new Trainer(1L, "John", "Smith", "nutrition coaching"),
                new Trainer(2L, "Alice", "Brown", "youth fitness")
        );
        when(trainerDao.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerService.getAllTrainers();

        assertEquals(2, result.size());
        verify(trainerDao, times(1)).findAll();
    }
}
