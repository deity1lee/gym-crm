package org.workload.controller;

import org.workload.dto.TrainerWorkloadRequest;
import org.workload.service.WorkloadService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workload")
public class TrainerWorkloadController {

    private final WorkloadService workloadService;

    public TrainerWorkloadController(WorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @PostMapping
    public void changeWorkload(@RequestBody TrainerWorkloadRequest request) {
        workloadService.process(request);
    }

    @GetMapping("/{username}/trainingHours/{month}")
    public Long getTrainerWorkingHours(@PathVariable String username, @PathVariable int month) {
        return workloadService.getWorkingHours(username, month);
    }
}
