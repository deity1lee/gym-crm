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
import org.example.service.TraineeService;
import org.example.utilities.TrainingSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "Trainees")
@RestController
@RequestMapping("/trainees")
public class TraineeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeController.class);

    private final TraineeService traineeService;
    private final TraineeMapper mapper;

    public TraineeController(TraineeService traineeService, TraineeMapper mapper) {
        this.traineeService = traineeService;
        this.mapper = mapper;
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

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] POST /trainees", txId);

        Trainee trainee = new Trainee(
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress(),
                true
        );

        Trainee created = traineeService.createTrainee(trainee);

        LOGGER.info("[{}] {} {}", txId,
                HttpStatus.CREATED.value(),
                HttpStatus.CREATED.getReasonPhrase());

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

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] GET /trainees/{}", txId, username);

        Trainee trainee = traineeService.selectTraineeByUsername(username);

        LOGGER.info("[{}] {} {}", txId,
                HttpStatus.OK.value(),
                HttpStatus.OK.getReasonPhrase());

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

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] PUT /trainees", txId);

        Trainee trainee = new Trainee(
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress(),
                request.getActive()
        );
        trainee.setUsername(request.getUsername());

        Trainee updated = traineeService.updateTrainee(trainee);

        LOGGER.info("[{}] {} {}", txId,
                HttpStatus.OK.value(),
                HttpStatus.OK.getReasonPhrase());

        return mapper.toResponse(updated);
    }

    @Operation(summary = "Delete trainee profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @DeleteMapping("/{username}")
    public void deleteTrainee(@PathVariable String username) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] DELETE /trainees/{}", txId, username);

        traineeService.deleteTraineeByUsername(username);

        LOGGER.info("[{}] {} {}", txId,
                HttpStatus.OK.value(),
                HttpStatus.OK.getReasonPhrase());
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

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] GET /trainees/{}/unassigned-trainers", txId, username);

        List<Trainer> trainers = traineeService.getUnassignedTrainers(username);

        List<TrainerInfoResponse> response = trainers.stream()
                .map(t -> new TrainerInfoResponse(
                        t.getUsername(),
                        t.getFirstName(),
                        t.getLastName(),
                        t.getTrainingType()
                ))
                .toList();

        LOGGER.info("[{}] {} {}", txId,
                HttpStatus.OK.value(),
                HttpStatus.OK.getReasonPhrase());

        return response;
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

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] PUT /trainees/{}/trainers", txId, username);

        List<String> trainerUsernames = request.getTrainers().stream()
                .map(UpdateTraineeTrainersRequest.TrainerUsernameRequest::getUsername)
                .toList();

        List<Trainer> trainers = traineeService.updateTraineeTrainers(username, trainerUsernames);

        List<TrainerInfoResponse> response = trainers.stream()
                .map(t -> new TrainerInfoResponse(
                        t.getUsername(),
                        t.getFirstName(),
                        t.getLastName(),
                        t.getTrainingType()
                ))
                .toList();

        LOGGER.info("[{}] {} {}", txId,
                HttpStatus.OK.value(),
                HttpStatus.OK.getReasonPhrase());

        return response;
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

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] GET /trainees/{}/trainings", txId, username);

        TrainingSearchCriteria criteria =
                new TrainingSearchCriteria(periodFrom, periodTo, trainerUsername, username, trainingTypeId);

        List<Training> trainings = traineeService.getTraineeTrainings(username, criteria);

        List<TrainingInfoResponse> response = new ArrayList<>();
        for (Training t : trainings) {
            response.add(new TrainingInfoResponse(
                    t.getName(),
                    t.getDate(),
                    new TrainingType(t.getTrainingType().getTrainingTypeName()),
                    t.getDuration(),
                    t.getTrainee().getFirstName() + " " + t.getTrainee().getLastName()
            ));
        }

        LOGGER.info("[{}] {} {}", txId,
                HttpStatus.OK.value(),
                HttpStatus.OK.getReasonPhrase());

        return response;
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

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] PATCH /trainees/{}/activation", txId, username);

        traineeService.updateActivationStatus(username, isActive);

        LOGGER.info("[{}] {} {}", txId,
                HttpStatus.OK.value(),
                HttpStatus.OK.getReasonPhrase());
    }
}
