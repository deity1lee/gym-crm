package com.example.secondarymicroservice.controller;

import com.example.secondarymicroservice.dto.TrainerWorkloadRequest;
import com.example.secondarymicroservice.service.TrainerWorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workload")
public class TrainerWorkloadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerWorkloadController.class);
    private final TrainerWorkloadService workloadService;

    public TrainerWorkloadController(TrainerWorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @PostMapping
    public void acceptWorkload(@RequestBody TrainerWorkloadRequest request) {
        String txId = MDC.get("transactionId");

        LOGGER.info(
                "[{}] POST /workload trainer={}",
                txId,
                request.getTrainerUsername()
        );
        workloadService.process(request);
    }
}
