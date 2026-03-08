package org.workload.unit;

import org.workload.dto.ActionType;
import org.workload.dto.TrainerWorkloadRequest;
import org.workload.entities.TrainerMonth;
import org.workload.entities.TrainerWorkload;
import org.workload.entities.TrainerYear;
import org.workload.repository.TrainerWorkloadRepository;
import org.workload.service.ValidatorService;
import org.workload.service.WorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkloadServiceTest {

    @Mock
    private TrainerWorkloadRepository repository;

    @Mock
    private ValidatorService validatorService;

    @InjectMocks
    private WorkloadService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doNothing().when(validatorService).validateTrainerWorkloadRequest(any());
    }

    @Test
    void shouldAddNewTrainerWorkload() {
        TrainerWorkloadRequest request = buildRequest(
                "alex",
                LocalDate.of(2026, 2, 5),
                120L,
                ActionType.ADD
        );

        when(repository.findByTrainerUsername("alex"))
                .thenReturn(Optional.empty());

        service.process(request);

        ArgumentCaptor<TrainerWorkload> captor =
                ArgumentCaptor.forClass(TrainerWorkload.class);

        verify(repository).save(captor.capture());

        TrainerWorkload saved = captor.getValue();
        TrainerMonth month = saved.getYears().get(0).getMonths().get(0);

        assertEquals("alex", saved.getTrainerUsername());
        assertEquals(2, month.getMonth());
        assertEquals(120L, month.getTrainingDuration());
    }

    @Test
    void shouldAddDurationToExistingMonth() {
        TrainerWorkload trainer = trainerWithMonth(2026, 2, 60L);

        TrainerWorkloadRequest request = buildRequest(
                "alex",
                LocalDate.of(2026, 2, 10),
                30L,
                ActionType.ADD
        );

        when(repository.findByTrainerUsername("alex"))
                .thenReturn(Optional.of(trainer));

        service.process(request);

        TrainerMonth month =
                trainer.getYears().get(0).getMonths().get(0);

        assertEquals(90L, month.getTrainingDuration());
        verify(repository).save(trainer);
    }

    @Test
    void shouldSubtractDuration() {
        TrainerWorkload trainer = trainerWithMonth(2026, 2, 50L);

        TrainerWorkloadRequest request = buildRequest(
                "alex",
                LocalDate.of(2026, 2, 5),
                30L,
                ActionType.DELETE
        );

        when(repository.findByTrainerUsername("alex"))
                .thenReturn(Optional.of(trainer));

        service.process(request);

        TrainerMonth month =
                trainer.getYears().get(0).getMonths().get(0);

        assertEquals(20L, month.getTrainingDuration());
        verify(repository).save(trainer);
    }

    @Test
    void shouldRemoveTrainerWhenDurationBecomesZero() {
        TrainerWorkload trainer = trainerWithMonth(2026, 2, 20L);

        TrainerWorkloadRequest request = buildRequest(
                "alex",
                LocalDate.of(2026, 2, 5),
                20L,
                ActionType.DELETE
        );

        when(repository.findByTrainerUsername("alex"))
                .thenReturn(Optional.of(trainer));

        service.process(request);

        verify(repository).delete(trainer);
        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnWorkingHours() {
        TrainerWorkload trainer = trainerWithMonth(2026, 2, 100L);

        when(repository.findByTrainerUsername("alex"))
                .thenReturn(Optional.of(trainer));

        Long hours = service.getWorkingHours("alex", 2026, 4);

        assertEquals(0L, hours);
    }

    @Test
    void shouldReturnZeroWhenTrainerNotFound() {
        when(repository.findByTrainerUsername("unknown"))
                .thenReturn(Optional.empty());

        assertEquals(0L, service.getWorkingHours("unknown", 2026, 4));
    }


    private TrainerWorkloadRequest buildRequest(
            String username,
            LocalDate date,
            Long duration,
            ActionType action
    ) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername(username);
        request.setTrainingDate(date);
        request.setTrainingDuration(duration);
        request.setActionType(action);
        return request;
    }

    private TrainerWorkload trainerWithMonth(int yearValue, int monthValue, long duration) {
        TrainerMonth month = new TrainerMonth();
        month.setMonth(monthValue);
        month.setTrainingDuration(duration);

        TrainerYear year = new TrainerYear();
        year.setYear(yearValue);
        year.getMonths().add(month);

        TrainerWorkload trainer = new TrainerWorkload();
        trainer.setTrainerUsername("alex");
        trainer.getYears().add(year);

        return trainer;
    }
}
