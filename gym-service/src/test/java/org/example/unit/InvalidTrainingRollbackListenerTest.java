package org.example.unit;

import org.example.dao.TrainingDao;
import org.example.dto.ActionType;
import org.example.dto.TrainerWorkloadRequest;
import org.example.entities.Training;
import org.example.service.InvalidTrainingRollbackListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class InvalidTrainingRollbackListenerTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private InvalidTrainingRollbackListener listener;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(listener, "apiKey", "valid-api-key");
    }

    @Test
    void testAddActionRollsBackByDeletingTraining() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setActionType(ActionType.ADD);

        Training training = new Training();
        when(trainingDao.findTrainingByTrainerWorkloadRequest(request))
                .thenReturn(training);

        listener.rollbackTrainings(request, "tx-1", "valid-api-key");

        verify(trainingDao, times(1)).deleteTraining(training);
        verify(trainingDao, never()).addTraining(any());
    }

    @Test
    void testInvalidApiKeyDoesNothing() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        assertThrows(BadCredentialsException.class, () -> listener.rollbackTrainings(request, "tx-3", "wrong-api-key"));

        verifyNoInteractions(trainingDao);
    }
}
