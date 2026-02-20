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
        request.setTrainingDuration(6L);
        request.setActionType(ActionType.ADD);

        boolean result = validatorService.validateAddRequest(request);
        assertTrue(result);
    }

    @Test
    void testPastDateReturnsFalse() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainingDate(LocalDate.now().minusDays(1));
        request.setTrainingDuration(6L);
        request.setActionType(ActionType.ADD);

        boolean result = validatorService.validateAddRequest(request);

        assertFalse(result, "Past ADD request should be invalid");
    }

    @Test
    void testPastDateWithDeleteReturnsTrue() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainingDate(LocalDate.now().minusDays(1));
        request.setTrainingDuration(6L);
        request.setActionType(ActionType.DELETE);

        boolean result = validatorService.validateAddRequest(request);

        assertTrue(result, "Past DELETE request should be valid");
    }

    @Test
    void testWorkloadWithinLimitReturnsTrue() {
        boolean result = validatorService.validateWorkloadLimit(100L);
        assertTrue(result, "Workload below max limit should be valid");
    }

    @Test
    void testWorkloadExactlyAtLimitReturnsTrue() {
        boolean result = validatorService.validateWorkloadLimit(120L);
        assertTrue(result, "Workload exactly at max limit should be valid");
    }

    @Test
    void testWorkloadExceedsLimitReturnsFalse() {
        boolean result = validatorService.validateWorkloadLimit(121L);
        assertFalse(result, "Workload above max limit should be invalid");
    }
}
