package org.workload.controller;

import org.workload.service.WorkloadService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workload")
public class TrainerWorkloadController {

    private final WorkloadService workloadService;

    public TrainerWorkloadController(WorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @GetMapping("/{username}/trainingHours")
    public Long getTrainerWorkingHours(@PathVariable String username, @RequestParam int year, @RequestParam int month) {
        return workloadService.getWorkingHours(username, year, month);
    }
}
