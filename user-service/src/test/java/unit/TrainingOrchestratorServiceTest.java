package unit;

import org.example.dto.TrainerWorkloadRequest;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
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

class TrainingOrchestratorServiceTest {

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainerWorkloadClient trainerWorkloadClient;

    @InjectMocks
    private TrainingOrchestratorService orchestratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTraining_success() {
        Training training = createTraining(1L);

        when(trainingService.addTraining(
                anyString(),
                anyString(),
                anyString(),
                any(TrainingType.class),
                any(LocalDate.class),
                anyLong()
        )).thenReturn(training);

        orchestratorService.addTraining(
                "trainee1",
                "trainer1",
                "Yoga",
                new TrainingType(),
                LocalDate.now(),
                60L
        );

        verify(trainingService).addTraining(
                eq("trainee1"),
                eq("trainer1"),
                eq("Yoga"),
                any(TrainingType.class),
                any(LocalDate.class),
                eq(60L)
        );

        verify(trainerWorkloadClient)
                .changeWorkingHours(any());

        verify(trainingService, never()).deleteTraining(anyLong());
    }

    @Test
    void addTraining_downstreamFailure_triggersRollback() {
        Training training = createTraining(1L);

        when(trainingService.addTraining(any(), any(), any(), any(), any(), any()))
                .thenReturn(training);

        doThrow(new DownstreamServiceException("workload service down"))
                .when(trainerWorkloadClient)
                .changeWorkingHours(any(TrainerWorkloadRequest.class));

        SagaFailedException ex = assertThrows(
                SagaFailedException.class,
                () -> orchestratorService.addTraining(
                        "trainee1",
                        "trainer1",
                        "Yoga",
                        new TrainingType(),
                        LocalDate.now(),
                        60L
                )
        );

        assertEquals("Failed to create the training", ex.getMessage());

        verify(trainingService).deleteTraining(1L);
    }

    @Test
    void addTraining_nonDownstreamFailure_noRollback() {
        when(trainingService.addTraining(any(), any(), any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("validation failed"));

        assertThrows(
                IllegalArgumentException.class,
                () -> orchestratorService.addTraining(
                        "trainee1",
                        "trainer1",
                        "Yoga",
                        new TrainingType(),
                        LocalDate.now(),
                        60L
                )
        );

        verify(trainingService, never()).deleteTraining(anyLong());
        verify(trainerWorkloadClient, never()).changeWorkingHours(any());
    }

    @Test
    void deleteTraining_success() {
        Training training = createTraining(1L);

        when(trainingService.findTrainingById(1L)).thenReturn(training);

        orchestratorService.deleteTraining(1L);

        verify(trainingService).deleteTraining(1L);
        verify(trainerWorkloadClient)
                .changeWorkingHours(any(TrainerWorkloadRequest.class));
    }

    @Test
    void deleteTraining_downstreamFailure_triggersRecreate() {
        Training training = createTraining(1L);

        when(trainingService.findTrainingById(1L)).thenReturn(training);

        doThrow(new DownstreamServiceException("workload error"))
                .when(trainerWorkloadClient)
                .changeWorkingHours(any(TrainerWorkloadRequest.class));

        SagaFailedException ex = assertThrows(
                SagaFailedException.class,
                () -> orchestratorService.deleteTraining(1L)
        );

        assertEquals("Failed to delete the training", ex.getMessage());

        verify(trainingService).addTraining(
                eq("trainee1"),
                eq("trainer1"),
                eq("Yoga"),
                any(),
                any(),
                eq(60L)
        );
    }

    @Test
    void deleteTraining_nonDownstreamFailure_rethrows() {
        when(trainingService.findTrainingById(1L))
                .thenThrow(new IllegalStateException("db error"));

        assertThrows(
                IllegalStateException.class,
                () -> orchestratorService.deleteTraining(1L)
        );

        verify(trainerWorkloadClient, never()).changeWorkingHours(any());
    }

    private Training createTraining(Long id) {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");

        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");

        Training training = new Training();
        training.setId(id);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setName("Yoga");
        training.setTrainingType(new TrainingType());
        training.setDate(LocalDate.now());
        training.setDuration(60L);

        return training;
    }
}
