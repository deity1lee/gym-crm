package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.*;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.example.mapper.TraineeMapper;
import org.example.orchestrator.TraineeOrchestratorDeletionPolicyService;
import org.example.service.TraineeService;
import org.example.utilities.TrainingSearchCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Trainees")
@RestController
@RequestMapping("/trainees")
public class TraineeController {

    private final TraineeService traineeService;
    private final TraineeMapper mapper;
    private final TraineeOrchestratorDeletionPolicyService deletionPolicyService;

    public TraineeController(TraineeService traineeService, TraineeMapper mapper, TraineeOrchestratorDeletionPolicyService deletionPolicyService) {
        this.traineeService = traineeService;
        this.mapper = mapper;
        this.deletionPolicyService = deletionPolicyService;
    }

    @Operation(
            summary = "Register a new trainee",
            description = "Creates a trainee profile and returns generated username and password"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainee successfully registered"),
            @ApiResponse(responseCode = "400", description = "Validation or business error"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserCredentials registerTrainee(@Valid @RequestBody TraineeRegistrationRequest request) {

        Trainee trainee = new Trainee(
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress(),
                true
        );

        Trainee created = traineeService.createTrainee(trainee);

        return new UserCredentials(
                created.getUsername(),
                created.getRawPassword()
        );
    }

    @Operation(
            summary = "Get trainee profile by username",
            description = "Returns a trainee profile based on provided username"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee profile retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation or business error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{username}")
    public TraineeProfileResponse getTraineeProfile(@PathVariable String username) {

        Trainee trainee = traineeService.selectTraineeByUsername(username);
        return mapper.toResponse(trainee);
    }

    @Operation(summary = "Update trainee profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping
    public TraineeProfileResponse updateTraineeProfile(
            @Valid @RequestBody UpdateTraineeProfileRequest request) {

        Trainee trainee = new Trainee(
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress(),
                request.getActive()
        );
        trainee.setUsername(request.getUsername());

        Trainee updated = traineeService.updateTrainee(trainee);
        return mapper.toResponse(updated);
    }

    @Operation(summary = "Delete trainee profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "503", description = "Service side error occurred")
    })
    @DeleteMapping("/{username}")
    public void deleteTrainee(@PathVariable String username) {

        deletionPolicyService.deleteTraineeByUsername(username);
    }

    @Operation(summary = "Get active trainers not assigned to trainee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{username}/unassigned-trainers")
    public List<TrainerInfoResponse> getUnassignedActiveTrainers(@PathVariable String username) {

        List<Trainer> trainers = traineeService.getUnassignedTrainers(username);

        return trainers.stream()
                .map(t -> new TrainerInfoResponse(
                        t.getUsername(),
                        t.getFirstName(),
                        t.getLastName(),
                        t.getTrainingType()
                ))
                .toList();
    }

    @Operation(summary = "Update trainee's trainers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainers updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{username}/trainers")
    public List<TrainerInfoResponse> updateTraineeTrainers(
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeTrainersRequest request) {

        List<String> trainerUsernames = request.getTrainers().stream()
                .map(UpdateTraineeTrainersRequest.TrainerUsernameRequest::getUsername)
                .toList();

        List<Trainer> trainers = traineeService.updateTraineeTrainers(username, trainerUsernames);

        return trainers.stream()
                .map(t -> new TrainerInfoResponse(
                        t.getUsername(),
                        t.getFirstName(),
                        t.getLastName(),
                        t.getTrainingType()
                ))
                .toList();
    }

    @Operation(summary = "Get trainee's trainings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee's trainings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{username}/trainings")
    public List<TrainingInfoResponse> getTraineeTrainings(
            @PathVariable String username,
            @RequestParam(required = false) Date periodFrom,
            @RequestParam(required = false) Date periodTo,
            @RequestParam(required = false) String trainerUsername,
            @RequestParam(required = false) Long trainingTypeId) {

        TrainingSearchCriteria criteria =
                new TrainingSearchCriteria(periodFrom, periodTo, trainerUsername, username, trainingTypeId);

        List<Training> trainings = traineeService.getTraineeTrainings(username, criteria);

        return trainings.stream()
                .map(t -> new TrainingInfoResponse(
                        t.getName(),
                        t.getDate(),
                        new TrainingType(t.getTrainingType().getTrainingTypeName()),
                        t.getDuration(),
                        String.format("%s %s", t.getTrainee().getFirstName(), t.getTrainee().getLastName())
                ))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Activate or deactivate trainee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activation updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{username}/activation")
    public void activateTraineeActivation(
            @PathVariable String username,
            @Valid @RequestBody Boolean isActive) {

        traineeService.updateActivationStatus(username, isActive);
    }
}
