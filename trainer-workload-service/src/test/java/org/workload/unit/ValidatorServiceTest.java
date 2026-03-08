package org.workload.unit;

import org.workload.dto.TrainerWorkloadRequest;
import org.workload.service.ValidatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorServiceTest {

    private ValidatorService validatorService;

    @BeforeEach
    void setUp() {
        validatorService = new ValidatorService();
        MDC.put("transactionId", "test-tx-id");
    }

    @Test
    void shouldPassValidationForFutureTrainingDate() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainingDate(LocalDate.now().plusDays(1));

        assertDoesNotThrow(() ->
                validatorService.validateTrainerWorkloadRequest(request)
        );
    }

    @Test
    void shouldFailValidationForPastTrainingDate() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainingDate(LocalDate.now().minusDays(1));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validatorService.validateTrainerWorkloadRequest(request)
        );

        assertEquals("Training has already taken place", exception.getMessage());
    }

    @Test
    void shouldFailValidationForTodayDate() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainingDate(LocalDate.now());

        assertDoesNotThrow(() ->
                validatorService.validateTrainerWorkloadRequest(request)
        );
    }
}
