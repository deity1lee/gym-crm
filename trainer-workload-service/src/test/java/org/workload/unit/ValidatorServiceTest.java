package org.workload.unit;

import org.workload.dto.ActionType;
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
    void testValidRequestReturnsTrue() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainingDate(LocalDate.now().plusDays(1));
        request.setTrainingDuration(120L);

        boolean result = validatorService.validateAddRequest(request);

        assertTrue(result);
    }

    @Test
    void testPastDateReturnsFalse() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainingDate(LocalDate.now().minusDays(1));
        request.setTrainingDuration(60L);
        request.setActionType(ActionType.ADD);

        boolean result = validatorService.validateAddRequest(request);

        assertFalse(result);
    }
}
