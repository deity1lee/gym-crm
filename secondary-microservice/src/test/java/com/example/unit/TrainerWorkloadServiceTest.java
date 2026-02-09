package com.example.unit;

import com.example.secondarymicroservice.dto.ActionType;
import com.example.secondarymicroservice.dto.TrainerWorkloadRequest;
import com.example.secondarymicroservice.entities.TrainerMonth;
import com.example.secondarymicroservice.entities.TrainerWorkload;
import com.example.secondarymicroservice.entities.TrainerYear;
import com.example.secondarymicroservice.repository.TrainerWorkloadRepository;
import com.example.secondarymicroservice.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerWorkloadServiceTest {

    @Mock
    private TrainerWorkloadRepository repository;

    @InjectMocks
    private TrainerWorkloadService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessAddsNewTrainer() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("alex");
        request.setTrainerFirstName("Alex");
        request.setTrainerLastName("Smith");
        request.setActive(true);
        request.setTrainingDate(LocalDate.of(2026, 2, 5));
        request.setTrainingDuration(120L);
        request.setActionType(ActionType.ADD);

        when(repository.findByTrainerUsername("alex")).thenReturn(Optional.empty());

        service.process(request);

        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(repository).save(captor.capture());

        TrainerWorkload saved = captor.getValue();
        assertEquals("alex", saved.getTrainerUsername());
        assertEquals("Alex", saved.getFirstName());
        assertEquals("Smith", saved.getLastName());
        assertTrue(saved.isActive());

        assertNotNull(saved.getYears());
        TrainerYear year = saved.getYears().get(0);
        assertNotNull(year.getMonths());
        TrainerMonth month = year.getMonths().get(0);
        assertEquals(2, month.getMonth());
        assertEquals(120L, month.getTotalDuration());
    }

    @Test
    void testProcessAddsDurationToExistingMonth() {
        TrainerMonth month = new TrainerMonth();
        month.setMonth(2);
        month.setTotalDuration(60L);

        TrainerYear year = new TrainerYear();
        year.setYear(2026);
        year.getMonths().add(month);

        TrainerWorkload existingTrainer = new TrainerWorkload();
        existingTrainer.setTrainerUsername("alex");
        existingTrainer.getYears().add(year);

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("alex");
        request.setTrainingDate(LocalDate.of(2026, 2, 5));
        request.setTrainingDuration(30L);
        request.setActionType(ActionType.ADD);

        when(repository.findByTrainerUsername("alex")).thenReturn(Optional.of(existingTrainer));

        service.process(request);

        verify(repository).save(existingTrainer);
        assertEquals(90L, existingTrainer.getYears().get(0).getMonths().get(0).getTotalDuration());
    }

    @Test
    void testProcessSubtractsDuration() {
        TrainerMonth month = new TrainerMonth();
        month.setMonth(2);
        month.setTotalDuration(50L);

        TrainerYear year = new TrainerYear();
        year.setYear(2026);
        year.getMonths().add(month);

        TrainerWorkload trainer = new TrainerWorkload();
        trainer.setTrainerUsername("alex");
        trainer.getYears().add(year);

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("alex");
        request.setTrainingDate(LocalDate.of(2026, 2, 5));
        request.setTrainingDuration(30L);
        request.setActionType(ActionType.DELETE);

        when(repository.findByTrainerUsername("alex")).thenReturn(Optional.of(trainer));

        service.process(request);

        verify(repository).save(trainer);
        assertEquals(20L, trainer.getYears().get(0).getMonths().get(0).getTotalDuration());
    }

    @Test
    void testProcessDoesNotGoBelowZero() {
        TrainerMonth month = new TrainerMonth();
        month.setMonth(2);
        month.setTotalDuration(20L);

        TrainerYear year = new TrainerYear();
        year.setYear(2026);
        year.getMonths().add(month);

        TrainerWorkload trainer = new TrainerWorkload();
        trainer.setTrainerUsername("alex");
        trainer.getYears().add(year);

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("alex");
        request.setTrainingDate(LocalDate.of(2026, 2, 5));
        request.setTrainingDuration(50L);
        request.setActionType(ActionType.DELETE);

        when(repository.findByTrainerUsername("alex"))
                .thenReturn(Optional.of(trainer));

        service.process(request);

        verify(repository).delete(trainer);
        verify(repository, never()).save(any());
    }
}