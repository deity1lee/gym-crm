package unit;

import org.example.dto.ActionType;
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
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeOrchestratorServiceTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerWorkloadClient trainerWorkloadClient;

    @InjectMocks
    private TraineeOrchestratorService orchestratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteTraineeSaga_success() {
        Trainee trainee = createTraineeWithTrainings("trainee1", 2);

        when(traineeService.selectTraineeByUsername("trainee1"))
                .thenReturn(trainee);

        orchestratorService.deleteTraineeSaga("trainee1");

        verify(trainerWorkloadClient, times(2))
                .changeWorkingHours(any(TrainerWorkloadRequest.class));

        verify(traineeService).deleteTrainee("trainee1");
    }

    @Test
    void deleteTraineeSaga_downstreamFailure_triggersRollback() {
        Trainee trainee = createTraineeWithTrainings("trainee1", 2);

        when(traineeService.selectTraineeByUsername("trainee1"))
                .thenReturn(trainee);

        doThrow(new DownstreamServiceException("workload service down"))
                .when(trainerWorkloadClient)
                .changeWorkingHours(any(TrainerWorkloadRequest.class));

        SagaFailedException ex = assertThrows(
                SagaFailedException.class,
                () -> orchestratorService.deleteTraineeSaga("trainee1")
        );

        assertEquals("Failed to complete trainee deletion saga", ex.getMessage());

        verify(trainerWorkloadClient, atLeastOnce())
                .changeWorkingHours(any(TrainerWorkloadRequest.class));

        verify(traineeService, never()).deleteTrainee(anyString());
    }

    @Test
    void deleteTraineeSaga_nonDownstreamFailure_rethrows() {
        Trainee trainee = createTraineeWithTrainings("trainee1", 1);

        when(traineeService.selectTraineeByUsername("trainee1"))
                .thenReturn(trainee);

        doThrow(new IllegalStateException("unexpected error"))
                .when(trainerWorkloadClient)
                .changeWorkingHours(any(TrainerWorkloadRequest.class));

        assertThrows(
                IllegalStateException.class,
                () -> orchestratorService.deleteTraineeSaga("trainee1")
        );

        verify(traineeService, never()).deleteTrainee(anyString());
    }

    private Trainee createTraineeWithTrainings(String username, int trainingCount) {
        Trainee trainee = new Trainee();
        trainee.setUsername(username);

        List<Training> trainings = IntStream.range(0, trainingCount)
                .mapToObj(i -> {
                    Training t = new Training();
                    t.setTrainee(trainee);
                    Trainer trainer = new Trainer();
                    trainer.setUsername("trainer" + i);
                    t.setTrainer(trainer);
                    return t;
                })
                .toList();

        trainee.setTrainings(trainings);
        return trainee;
    }
}
