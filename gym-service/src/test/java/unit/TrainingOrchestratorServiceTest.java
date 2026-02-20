package unit;

import org.example.dao.TrainingDao;
import org.example.dto.ActionType;
import org.example.dto.TrainerWorkloadRequest;
import org.example.entities.*;
import org.example.exception.DownstreamServiceException;
import org.example.exception.SagaFailedException;
import org.example.orchestrator.TrainingOrchestratorService;
import org.example.service.TrainerWorkloadClient;
import org.example.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.slf4j.MDC;

class TrainingOrchestratorServiceTest {

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainerWorkloadClient trainerWorkloadClient;

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingOrchestratorService trainingOrchestratorService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        MDC.put("transactionId", "test-tx");
    }

    @Test
    void addTraining_success() {
        TrainingType type = new TrainingType();
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");

        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setActive(true);

        Training training = new Training();
        training.setId(1L);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setName("Yoga");
        training.setDate(LocalDate.now());
        training.setDuration(6L);
        training.setTrainingType(type);

        when(trainingService.addTraining(anyString(), anyString(), anyString(), any(), any(), anyLong()))
                .thenReturn(training);

        trainingOrchestratorService.addTraining("trainee1", "trainer1", "Yoga", type, LocalDate.now(), 60L);

        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(trainerWorkloadClient).changeTrainerWorkload(captor.capture());

        TrainerWorkloadRequest request = captor.getValue();
        assertEquals(ActionType.ADD, request.getActionType());
        assertEquals("trainer1", request.getTrainerUsername());
    }

    @Test
    void addTraining_downstreamFails_deletesCreatedTrainingAndThrows() {
        TrainingType type = new TrainingType();

        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");

        Training training = new Training();
        training.setId(1L);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setName("Yoga");
        training.setDate(LocalDate.now());
        training.setDuration(6L);
        training.setTrainingType(type);

        when(trainingService.addTraining(anyString(), anyString(), anyString(), any(), any(), anyLong()))
                .thenReturn(training);

        doThrow(new DownstreamServiceException("fail", null))
                .when(trainerWorkloadClient).changeTrainerWorkload(any());

        SagaFailedException ex = assertThrows(SagaFailedException.class, () ->
                trainingOrchestratorService.addTraining("trainee1", "trainer1", "Yoga", type, LocalDate.now(), 60L)
        );

        verify(trainingService).deleteTraining(1L);
        assertTrue(ex.getMessage().contains("Failed to create the training"));
    }

    @Test
    void deleteTraining_success() {
        Training training = new Training();
        training.setId(10L);
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        training.setTrainee(trainee);
        training.setTrainer(trainer);

        when(trainingService.findTrainingById(10L)).thenReturn(training);

        trainingOrchestratorService.deleteTraining(10L);

        verify(trainingService).deleteTraining(10L);

        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(trainerWorkloadClient).changeTrainerWorkload(captor.capture());

        when(trainingDao.findTrainingByTrainerWorkloadRequest(captor.getValue())).thenReturn(training);

        assertEquals(ActionType.DELETE, captor.getValue().getActionType());
        assertEquals(training, trainingDao.findTrainingByTrainerWorkloadRequest(captor.getValue()));
    }

    @Test
    void deleteTraining_downstreamFails_recreatesTrainingAndThrows() {
        Training training = new Training();
        training.setId(10L);
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");
        training.setTrainee(trainee);
        training.setTrainer(trainer);

        when(trainingService.findTrainingById(10L)).thenReturn(training);
        doThrow(new DownstreamServiceException("fail", null))
                .when(trainerWorkloadClient).changeTrainerWorkload(any());

        SagaFailedException ex = assertThrows(SagaFailedException.class, () ->
                trainingOrchestratorService.deleteTraining(10L)
        );

        verify(trainingService).addTraining(
                eq("trainee1"),
                eq("trainer1"),
                eq(training.getName()),
                eq(training.getTrainingType()),
                eq(training.getDate()),
                eq(training.getDuration())
        );

        assertTrue(ex.getMessage().contains("Failed to delete the training"));
    }
}
