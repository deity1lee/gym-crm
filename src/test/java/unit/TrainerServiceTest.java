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
import org.slf4j.MDC;

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
        MDC.put("transactionId", "test-tx");
    }

    @Test
    void createTrainer_success() {
        Trainer trainer = new Trainer();

        doNothing().when(validatorService).validate(trainer);
        doNothing().when(authService).assignCredentials(trainer);

        trainerService.createTrainer(trainer);

        verify(validatorService).validate(trainer);
        verify(authService).assignCredentials(trainer);
        verify(trainerDao).create(trainer);
    }

    @Test
    void selectTrainerByUsername_found() {
        Trainer trainer = new Trainer();
        trainer.setTrainees(new ArrayList<>());

        when(trainerDao.findByUsername("alex")).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.selectTrainerByUsername("alex");

        assertNotNull(result);
        verify(trainerDao).findByUsername("alex");
    }

    @Test
    void selectTrainerByUsername_notFound() {
        when(trainerDao.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.selectTrainerByUsername("ghost"));
    }

    @Test
    void updateTrainer_success() {
        Trainer existing = new Trainer();
        existing.setUsername("alex");
        existing.setTrainees(new ArrayList<>()); // prevent NPE

        Trainer incoming = new Trainer();
        incoming.setUsername("alex");
        incoming.setFirstName("A");
        incoming.setLastName("B");
        incoming.setActive(true);
        incoming.setTrainingType(new TrainingType()); // mock training type

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
    void updateActivationStatus_success() {
        Trainer trainer = new Trainer();
        trainer.setUsername("alex");

        when(trainerDao.findByUsername("alex")).thenReturn(Optional.of(trainer));

        trainerService.updateActivationStatus("alex", true);

        assertTrue(trainer.isActive());
        verify(trainerDao).updateProfile(trainer);
    }

    @Test
    void updateActivationStatus_notFound() {
        when(trainerDao.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.updateActivationStatus("ghost", true));
    }

    @Test
    void getTrainerTrainings_success() {
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
    void getTrainerTrainings_missingTrainerUsername() {
        TrainingSearchCriteria criteria =
                new TrainingSearchCriteria(null, null, "", null, null);

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.getTrainerTrainings(criteria));
    }

    @Test
    void getTrainerTrainings_trainerNotFound() {
        TrainingSearchCriteria criteria =
                new TrainingSearchCriteria(null, null, "alex", null, null);

        when(trainerDao.findByUsername("alex")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.getTrainerTrainings(criteria));
    }

    @Test
    void getTrainerTrainings_traineeFilterNotFound() {
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
    void getTrainerTrainings_invalidDateRange() {
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
