package org.workload.unit;

import org.workload.entities.TrainerMonth;
import org.workload.entities.TrainerWorkload;
import org.workload.entities.TrainerYear;
import org.workload.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.workload.service.WorkloadService;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkloadServiceTest {

    @Mock
    private TrainerWorkloadRepository repository;

    @InjectMocks
    private WorkloadService workloadService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetWorkingHoursReturnsDuration() {
        TrainerMonth month = new TrainerMonth();
        month.setMonth(5);
        month.setTrainingDuration(180L);

        TrainerYear year = new TrainerYear();
        year.setYear(2025);
        year.getMonths().add(month);
        year.getMonths().add(month);

        TrainerWorkload trainer = new TrainerWorkload();
        trainer.getYears().add(year);

        when(repository.findByTrainerUsername("john"))
                .thenReturn(Optional.of(trainer));

        Long result = workloadService.getWorkingHours("john", 2025, 5);

        assertEquals(180L, result);
    }

    @Test
    void testGetWorkingHoursTrainerNotFoundReturnsZero() {
        when(repository.findByTrainerUsername("unknown"))
                .thenReturn(Optional.empty());

        Long result = workloadService.getWorkingHours("unknown", 2024, 6);

        assertEquals(0L, result);
    }

    @Test
    void testGetWorkingHoursMonthNotFoundReturnsZero() {
        TrainerWorkload trainer = new TrainerWorkload();
        when(repository.findByTrainerUsername("john"))
                .thenReturn(Optional.of(trainer));

        Long result = workloadService.getWorkingHours("john", 2024, 12);

        assertEquals(0L, result);
    }
}
