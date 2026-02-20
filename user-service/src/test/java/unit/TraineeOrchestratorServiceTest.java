package unit;

import org.example.dto.TrainerWorkloadRequest;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.exception.DownstreamServiceException;
import org.example.exception.SagaFailedException;
import org.example.orchestrator.TraineeOrchestratorService;
import org.example.service.TraineeService;
import org.example.service.TrainerWorkloadClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeOrchestratorServiceTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerWorkloadClient trainerWorkloadClient;

    @InjectMocks
    private TraineeOrchestratorService orchestrator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteTraineeSagaSuccess() {
        Trainee trainee = new Trainee();

        Trainer trainer1 = new Trainer();
        Trainer trainer2 = new Trainer();

        Training training1 = new Training();
        training1.setTrainer(trainer1);

        Training training2 = new Training();
        training2.setTrainer(trainer2);

        trainee.setTrainings(List.of(training1, training2));
        trainee.setTrainers(List.of(trainer1, trainer2));

        when(traineeService.selectTraineeByUsername("alex"))
                .thenReturn(trainee);

        orchestrator.deleteTraineeSaga("alex");

        verify(traineeService).deleteTrainee("alex");
        verify(trainerWorkloadClient, times(2))
                .changeTrainerWorkload(any(TrainerWorkloadRequest.class));
    }

    @Test
    void testDeleteTraineeSagaDownstreamFailureTriggersRollback() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        Training training = new Training();
        training.setTrainer(trainer);
        trainee.setTrainings(List.of(training));
        trainee.setTrainers(List.of(trainer));

        when(traineeService.selectTraineeByUsername("alex"))
                .thenReturn(trainee);

        doThrow(new DownstreamServiceException("down", new RuntimeException()))
                .when(trainerWorkloadClient)
                .changeTrainerWorkload(any());

        assertThrows(SagaFailedException.class,
                () -> orchestrator.deleteTraineeSaga("alex"));

        verify(traineeService, never()).deleteTrainee(any());
        verify(traineeService, never()).createTrainee(any());
        verify(trainerWorkloadClient, atLeastOnce())
                .changeTrainerWorkload(any());
    }

    @Test
    void testDeleteTraineeSagaNonDownstreamExceptionIsRethrown() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        Training training = new Training();
        training.setTrainer(trainer);
        trainee.setTrainings(List.of(training));
        trainee.setTrainers(List.of(trainer));

        when(traineeService.selectTraineeByUsername("alex"))
                .thenReturn(trainee);

        doThrow(new IllegalStateException("fail"))
                .when(traineeService).deleteTrainee("alex");

        assertThrows(IllegalStateException.class,
                () -> orchestrator.deleteTraineeSaga("alex"));
        verify(traineeService, never()).createTrainee(any());
    }
}
