package unit;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.example.service.AuthService;
import org.example.service.TrainerService;
import org.example.service.ValidatorService;
import org.example.utilities.TrainingSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;
import java.sql.Date;

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

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTrainerSuccess() {
        Trainer trainer = new Trainer();

        doNothing().when(validatorService).validate(trainer);
        doNothing().when(authService).assignCredentials(trainer);

        trainerService.createTrainer(trainer);

        verify(validatorService).validate(trainer);
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
        verify(trainerDao).findByUsername("alex");
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

        verify(validatorService).validate(incoming);
        verify(validatorService).validateTrainingType(incoming.getTrainingType());
        verify(trainerDao).updateProfile(existing);
    }

    @Test
    void testUpdateActivationStatusSuccess() {
        Trainer trainer = new Trainer();
        trainer.setUsername("alex");

        when(trainerDao.findByUsername("alex")).thenReturn(Optional.of(trainer));

        trainerService.updateActivationStatus("alex", true);

        assertTrue(trainer.isActive());
        verify(trainerDao).updateProfile(trainer);
    }

    @Test
    void testUpdateActivationStatusNotFound() {
        when(trainerDao.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.updateActivationStatus("ghost", true));
    }

    @Test
    void testGetTrainerTrainingsSuccess() {
        Trainer trainer = new Trainer();
        when(trainerDao.findByUsername("alex")).thenReturn(Optional.of(trainer));

        TrainingSearchCriteria criteria =
                new TrainingSearchCriteria(null, null, "alex", null, null);

        List<Training> trainings = List.of(new Training(), new Training());
        when(trainerDao.findByTrainerUsernameAndCriteria(criteria))
                .thenReturn(trainings);

        List<Training> result = trainerService.getTrainerTrainings(criteria);

        assertEquals(2, result.size());
    }

    @Test
    void testGetTrainerTrainingsMissingTrainerUsername() {
        TrainingSearchCriteria criteria =
                new TrainingSearchCriteria(null, null, "", null, null);

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.getTrainerTrainings(criteria));
    }

    @Test
    void testGetTrainerTrainingsTrainerNotFound() {
        TrainingSearchCriteria criteria =
                new TrainingSearchCriteria(null, null, "alex", null, null);

        when(trainerDao.findByUsername("alex")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.getTrainerTrainings(criteria));
    }

    @Test
    void testGetTrainerTrainingsTraineeFilterNotFound() {
        TrainingSearchCriteria criteria =
                new TrainingSearchCriteria(null, null, "alex", "traineeX", null);

        when(trainerDao.findByUsername("alex"))
                .thenReturn(Optional.of(new Trainer()));

        when(traineeDao.findByUsername("traineeX"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.getTrainerTrainings(criteria));
    }

    @Test
    void testGetTrainerTrainingsInvalidDateRange() {
        Date from = new Date(200);
        Date to = new Date(100);

        TrainingSearchCriteria criteria =
                new TrainingSearchCriteria(from, to, "alex", null, null);

        when(trainerDao.findByUsername("alex"))
                .thenReturn(Optional.of(new Trainer()));

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.getTrainerTrainings(criteria));
    }
}
