package org.workload.unit;

import org.workload.dto.ActionType;
import org.workload.dto.TrainerWorkloadRequest;
import org.workload.entities.TrainerWorkload;
import org.workload.exception.WorkloadUpdateRequestAuthException;
import org.workload.repository.TrainerWorkloadRepository;
import org.workload.service.TrainerWorkloadChangeListener;
import org.workload.service.ValidatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.workload.dto.ActionType.ADD;

class TrainerWorkloadChangeListenerTest {

    @Mock
    private TrainerWorkloadRepository repository;

    @Mock
    private ValidatorService validatorService;

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private TrainerWorkloadChangeListener listener;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Inject @Value field manually
        ReflectionTestUtils.setField(listener, "apiKey", "valid-api-key");
    }

    @Test
    void testInvalidRequestWithInvalidApiKeyIsSentToInvalidQueue() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");

        when(validatorService.validateAddRequest(request)).thenReturn(false);

        assertThrows(WorkloadUpdateRequestAuthException.class, () -> listener.process(request, "tx-1", "wrong-api-key"));
    }
   @Test
    void testInvalidRequestWithValidApiKeyIsSentToInvalidQueue() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

     request.setTrainerUsername("john");
        request.setActionType(ADD);

        when(validatorService.validateAddRequest(request)).thenReturn(false);

        listener.process(request, "tx-2", "valid-api-key");

        verify(jmsTemplate, times(1))
                .convertAndSend(eq("invalid.trainer.workload"), eq(request), any());

        verifyNoInteractions(repository);
    }

    @Test
    void testDeleteIgnoredWhenTrainerNotFound() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setActionType(ActionType.DELETE);

        when(repository.findByTrainerUsername("john")).thenReturn(Optional.empty());

        listener.process(request, "tx-3", "valid-api-key");

        verify(repository, never()).save(any());
        verify(repository, never()).delete(any());
    }

    @Test
    void testAddCreatesAndSavesTrainerWorkload() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainerFirstName("John");
        request.setTrainerLastName("Doe");
        request.setActive(true);
        request.setTrainingDuration(60L);
        request.setTrainingDate(LocalDate.now().plusDays(1));
        request.setActionType(ADD);

        when(validatorService.validateAddRequest(request)).thenReturn(true);
        when(repository.findByTrainerUsername("john")).thenReturn(Optional.empty());

        listener.process(request, "tx-4", "valid-api-key");

        verify(repository, times(1)).save(any(TrainerWorkload.class));
    }
}
