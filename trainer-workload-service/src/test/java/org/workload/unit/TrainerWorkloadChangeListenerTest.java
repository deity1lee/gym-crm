package org.workload.unit;

import org.workload.dto.ActionType;
import org.workload.dto.TrainerWorkloadRequest;
import org.workload.entities.TrainerMonth;
import org.workload.entities.TrainerWorkload;
import org.workload.entities.TrainerYear;
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
        ReflectionTestUtils.setField(listener, "apiKey", "valid-api-key");
    }

    @Test
    void testInvalidRequestWithInvalidApiKeyThrowsException() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");

        assertThrows(WorkloadUpdateRequestAuthException.class,
                () -> listener.process(request, "tx-1", "wrong-api-key"));
    }

    @Test
    void testInvalidRequestWithValidApiKeyIsSentToInvalidQueue() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setActionType(ActionType.ADD);

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
        request.setActionType(ActionType.ADD);

        when(validatorService.validateAddRequest(request)).thenReturn(true);
        when(repository.findByTrainerUsername("john")).thenReturn(Optional.empty());
        when(validatorService.validateWorkloadLimit(anyLong())).thenReturn(true);

        listener.process(request, "tx-4", "valid-api-key");

        verify(repository, times(1)).save(any(TrainerWorkload.class));
    }

    @Test
    void testAddUpdatesExistingTrainerWorkload() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setActionType(ActionType.ADD);
        request.setTrainingDuration(30L);
        request.setTrainingDate(LocalDate.of(2026, 3, 1));

        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setTrainerUsername("john");

        when(validatorService.validateAddRequest(request)).thenReturn(true);
        when(repository.findByTrainerUsername("john")).thenReturn(Optional.of(existingWorkload));
        when(validatorService.validateWorkloadLimit(anyLong())).thenReturn(true);

        listener.process(request, "tx-5", "valid-api-key");

        verify(repository, times(1)).save(existingWorkload);
    }

    @Test
    void testAddFailsDueToWorkloadLimit() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setActionType(ActionType.ADD);
        request.setTrainingDuration(999L);
        request.setTrainingDate(LocalDate.of(2026, 3, 1));

        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setTrainerUsername("john");

        when(validatorService.validateAddRequest(request)).thenReturn(true);
        when(repository.findByTrainerUsername("john")).thenReturn(Optional.of(existingWorkload));
        when(validatorService.validateWorkloadLimit(anyLong())).thenReturn(false);

        listener.process(request, "tx-6", "valid-api-key");

        verify(jmsTemplate, times(1)).convertAndSend(eq("invalid.trainer.workload"), eq(request), any());
        verify(repository, never()).save(existingWorkload);
    }

    @Test
    void testDeleteRemovesMonthAndYearAndTrainerWorkload() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setActionType(ActionType.DELETE);
        request.setTrainingDuration(60L);
        request.setTrainingDate(LocalDate.of(2026, 3, 1));

        TrainerWorkload workload = new TrainerWorkload();
        workload.setTrainerUsername("john");

        TrainerYear year = new TrainerYear();
        year.setYear(2026);
        TrainerMonth month = new TrainerMonth();
        month.setMonth(3);
        month.setTrainingDuration(60L);
        year.getMonths().add(month);
        workload.getYears().add(year);

        when(validatorService.validateAddRequest(request)).thenReturn(true);
        when(repository.findByTrainerUsername("john")).thenReturn(Optional.of(workload));
        when(validatorService.validateWorkloadLimit(anyLong())).thenReturn(true);

        listener.process(request, "tx-7", "valid-api-key");

        verify(repository, times(1)).delete(any(TrainerWorkload.class));
    }
}