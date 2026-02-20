package org.example.unit;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.example.service.*;
import org.example.utilities.TrainingSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;
    @Mock
    private TraineeDao traineeDao;
    @Mock
    private ValidatorService validatorService;
    @Mock
    private AuthService authService;
    @Mock
    private TrainerWorkloadClient trainerWorkloadClient;

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTrainerSuccess() {
        Trainer trainer = new Trainer();
        trainer.setTrainingType(new TrainingType());

        trainerService.createTrainer(trainer);

        verify(validatorService).validate(trainer);
        verify(validatorService).validateTrainingType(trainer.getTrainingType());
        verify(authService).assignCredentials(trainer);
        verify(trainerDao).create(trainer);
    }

    @Test
    void testSelectTrainerByUsernameFound() {
        Trainer trainer = new Trainer();
        trainer.setTrainees(new ArrayList<>());

        when(trainerDao.findByUsername("alex")).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.selectTrainerByUsername("alex");

        assertNotNull(result);
    }

    @Test
    void testSelectTrainerByUsernameNotFound() {
        when(trainerDao.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.selectTrainerByUsername("ghost"));
    }

    @Test
    void testUpdateTrainerSuccess() {
        Trainer existing = new Trainer();
        existing.setUsername("alex");
        existing.setTrainees(new ArrayList<>());

        Trainer incoming = new Trainer();
        incoming.setUsername("alex");
        incoming.setFirstName("A");
        incoming.setLastName("B");
        incoming.setActive(true);
        incoming.setTrainingType(new TrainingType());

        when(trainerDao.findByUsername("alex")).thenReturn(Optional.of(existing));

        Trainer updated = trainerService.updateTrainer(incoming);

        assertEquals("A", updated.getFirstName());
        assertEquals("B", updated.getLastName());
        assertTrue(updated.isActive());
        verify(trainerDao).updateProfile(existing);
    }

    @Test
    void testUpdateActivationStatusSuccess() {
        Trainer trainer = new Trainer();
        trainer.setUsername("alex");
        trainer.setActive(false);

        when(trainerDao.findByUsername("alex")).thenReturn(Optional.of(trainer));

        trainerService.updateActivationStatus("alex", true);

        assertTrue(trainer.isActive());
        verify(trainerDao).updateProfile(trainer);
    }

    @Test
    void testUpdateActivationStatusAlreadyActiveThrows() {
        Trainer trainer = new Trainer();
        trainer.setActive(true);

        when(trainerDao.findByUsername("alex")).thenReturn(Optional.of(trainer));

        assertThrows(IllegalStateException.class,
                () -> trainerService.updateActivationStatus("alex", true));
    }

    @Test
    void testGetTrainerTrainingsSuccess() {
        when(trainerDao.findByUsername("alex"))
                .thenReturn(Optional.of(new Trainer()));

        TrainingSearchCriteria criteria =
                new TrainingSearchCriteria(null, null, "alex", null, null);

        List<Training> trainings = List.of(new Training(), new Training());
        when(trainerDao.findByTrainerUsernameAndCriteria(criteria))
                .thenReturn(trainings);

        List<Training> result = trainerService.getTrainerTrainings(criteria);

        assertEquals(2, result.size());
    }

    @Test
    void testGetTrainingHoursSuccess() {
        when(trainerDao.findByUsername("alex"))
                .thenReturn(Optional.of(new Trainer()));
        when(trainerWorkloadClient.getTrainerTrainingHours("alex", 2025, 5))
                .thenReturn(100L);

        Long hours = trainerService.getTrainingHours("alex", 2025,5);

        assertEquals(100L, hours);
    }

    @Test
    void testGetTrainingHoursInvalidMonth() {
        when(trainerDao.findByUsername("alex"))
                .thenReturn(Optional.of(new Trainer()));

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.getTrainingHours("alex", 2027,13));
    }

    @Test
    void testGetTrainingHoursTrainerNotFound() {
        when(trainerDao.findByUsername("alex"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.getTrainingHours("alex", 2025, 6));
    }
}
