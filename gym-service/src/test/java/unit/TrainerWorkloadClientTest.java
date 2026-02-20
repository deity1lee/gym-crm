package unit;

import org.example.dto.TrainerWorkloadRequest;
import org.example.exception.DownstreamServiceException;
import org.example.service.TrainerWorkloadClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TrainerWorkloadClientTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TrainerWorkloadClient trainerWorkloadClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(trainerWorkloadClient, "apiKey", "valid-api-key");
    }

    @Test
    void testChangeTrainerWorkloadSendsMessage() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        trainerWorkloadClient.changeTrainerWorkload(request);

        verify(jmsTemplate, times(1))
                .convertAndSend(eq("trainer.workload.change.queue"), eq(request), any());
    }

    @Test
    void testFallbackThrowsDownstreamServiceException() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        assertThrows(DownstreamServiceException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        trainerWorkloadClient,
                        "changeWorkingHoursFailure",
                        request,
                        new RuntimeException("failure")
                )
        );
    }

    @Test
    void testGetTrainerTrainingHoursReturnsValue() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(Long.class)
        )).thenReturn(ResponseEntity.ok(120L));

        Long result = trainerWorkloadClient.getTrainerTrainingHours("john", 2026, 5);

        assertEquals(120L, result);
    }
}
