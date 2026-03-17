package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.*;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.example.mapper.TrainerMapper;
import org.example.service.TrainerService;
import org.example.utilities.TrainingSearchCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.sql.Date;
import java.util.List;

@Tag(name = "Trainers")
@RestController
@RequestMapping("/trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainerMapper mapper;

    public TrainerController(TrainerService trainerService, TrainerMapper mapper) {
        this.trainerService = trainerService;
        this.mapper = mapper;
    }

    @Operation(summary = "Register a new trainer", description = "Creates a trainer profile and returns generated username and password")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainer successfully registered"),
            @ApiResponse(responseCode = "400", description = "Validation or business error"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserCredentials registerTrainer(@Valid @RequestBody TrainerRegistrationRequest request) {

        Trainer trainer = new Trainer(
                request.getFirstName(),
                request.getLastName(),
                request.getTrainingType()
        );
        Trainer created = trainerService.createTrainer(trainer);
        return new UserCredentials(
                created.getUsername(),
                created.getRawPassword()
        );
    }

    @Operation(
            summary = "Get trainer profile by username",
            description = "Returns a trainer profile for the specified username"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer profile retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation or business error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized / invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{username}")
    public TrainerProfileResponse getTrainerProfile(@PathVariable String username) {

        Trainer trainer = trainerService.selectTrainerByUsername(username);
        return mapper.toResponse(trainer);
    }

    @Operation(description = "Update trainer profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation or business error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized / invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping
    public TrainerProfileResponse updateTrainerProfile(@Valid @RequestBody UpdateTrainerProfileRequest request) {

        Trainer updatedTrainerData = new Trainer(
                request.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                request.getTrainingType(),
                request.getActive()
        );
        Trainer updatedTrainer = trainerService.updateTrainer(updatedTrainerData);
        return mapper.toResponse(updatedTrainer);
    }

    @Operation(summary = "Retrieve trainee's trainings by trainee username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activation updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{username}/trainings")
    public List<TrainingInfoResponse> getTrainerTrainings(
            @PathVariable String username,
            @RequestParam(required = false) Date periodFrom,
            @RequestParam(required = false) Date periodTo,
            @RequestParam(required = false) String traineeUsername) {

        TrainingSearchCriteria criteria = new TrainingSearchCriteria(periodFrom, periodTo, username, traineeUsername, null);

        List<Training> trainings = trainerService.getTrainerTrainings(criteria);

        return trainings.stream()
                .map(t -> new TrainingInfoResponse(
                        t.getName(),
                        t.getDate(),
                        new TrainingType(t.getTrainingType().getId(), t.getTrainingType().getTrainingTypeName()),
                        t.getDuration(),
                        String.format("%s %s", t.getTrainer().getFirstName(), t.getTrainer().getLastName())
                ))
                .toList();
    }

    @Operation(
            summary = "Get trainer's working hours by month",
            description = "Returns amount of training hours for the specified username and month"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer workload retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation or business error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized / invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service side error occurred")
    })
    @GetMapping("/{username}/trainingHours")
    public Long getTrainerTrainingHours(@PathVariable String username, @RequestParam int year, @RequestParam int month) {
        return trainerService.getTrainingHours(username, year, month);
    }

    @Operation(summary = "Activate or deactivate trainee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activation updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("{username}/activation")
    public void updateTrainerActivation(@PathVariable String username, @Valid @RequestBody Boolean isActive) {
        trainerService.updateActivationStatus(username, isActive);
    }
}
