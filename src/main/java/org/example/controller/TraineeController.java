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
import java.util.ArrayList;
import java.sql.Date;
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

    @Operation(summary = "Register a new trainee",
            description = "Creates a trainee profile and returns generated username and password")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainee successfully registered"),
            @ApiResponse(responseCode = "400", description = "Validation or business error"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserCredentials registerTrainee(@Valid @RequestBody TraineeRegistrationRequest request) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] POST /trainee - registerTrainee(firstName='{}', lastName='{}')", txId, request.getFirstName(), request.getLastName());
        try {
            Trainee trainee = new Trainee(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getDateOfBirth(),
                    request.getAddress(),
                    true
            );
            Trainee created = traineeService.createTrainee(trainee);
            UserCredentials response = new UserCredentials(
                    created.getUsername(),
                    created.getPassword()
            );
            LOGGER.info("[{}] registerTrainee success - 201 CREATED - username={}", txId, created.getUsername());
            return response;
        } catch (Exception ex) {
            LOGGER.error("[{}] registerTrainee failed - 400 BAD REQUEST", txId);
            throw ex;
        }
    }

    @Operation(summary = "Get trainee profile by username",
            description = "Returns a trainee profile based on provided username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee profile retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation or business error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{username}")
    public TraineeProfileResponse getTraineeProfile(@PathVariable String username) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] GET /trainee/{username} - getTraineeProfile(username={})", txId, username);
        try {
            Trainee trainee = traineeService.selectTraineeByUsername(username);
            LOGGER.info("[{}] getTraineeProfile success - 200 OK - foundUsername={}", txId, trainee.getUsername());
            return mapper.toResponse(trainee);
        } catch (Exception ex) {
            LOGGER.error("[{}] getTraineeProfile failed - error={}", txId, ex.getMessage());
            throw ex;
        }
    }

    @Operation(summary = "Update trainee profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping
    public TraineeProfileResponse updateTraineeProfile(
            @Valid @RequestBody UpdateTraineeProfileRequest request) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] PUT /trainees - updateTraineeProfile(usernameForUpdate={})", txId, request.getUsername());
        try {
            Trainee newTrainee = new Trainee(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getDateOfBirth(),
                    request.getAddress(),
                    request.getActive()
            );
            newTrainee.setUsername(request.getUsername());
            Trainee updated = traineeService.updateTrainee(newTrainee);
            LOGGER.info("[{}] updateTraineeProfile success - 200 OK - updatedUsername={}", txId, updated.getUsername());
            return mapper.toResponse(updated);
        } catch (Exception ex) {
            LOGGER.error("[{}] updateTraineeProfile failed - {}", txId, ex.getMessage());
            throw ex;
        }
    }

    @Operation(summary = "Delete trainee profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "501", description = "Unsupported operation")
    })
    @DeleteMapping("/{username}")
    public void deleteTrainee(@PathVariable String username) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] DELETE /trainee/{username} - deleteTrainee(username={})", txId, username);
        try {
            traineeService.deleteTraineeByUsername(username);
            LOGGER.info("[{}] deleteTrainee success - 200 OK - deleted={}", txId, username);
        } catch (Exception ex) {
            LOGGER.error("[{}] deleteTrainee failed - {}", txId, ex.getMessage());
            throw ex;
        }
    }

    @Operation(summary = "Get active trainers not assigned to trainee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{username}/unassigned-trainers")
    public List<TrainerInfoResponse> getUnassignedActiveTrainers(@PathVariable String username) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] GET /trainee/{}/unassigned-trainers", txId, username);
        try {
            List<Trainer> trainers = traineeService.getUnassignedTrainers(username);
            List<TrainerInfoResponse> response = trainers.stream()
                    .map(t -> new TrainerInfoResponse(
                            t.getUsername(),
                            t.getFirstName(),
                            t.getLastName(),
                            t.getTrainingType()
                    ))
                    .toList();
            LOGGER.info("[{}] getUnassignedActiveTrainers success - {} trainers", txId, response.size());
            return response;
        } catch (Exception ex) {
            LOGGER.error("[{}] getUnassignedActiveTrainers failed - {}", txId, ex.getMessage());
            throw ex;
        }
    }

    @Operation(summary = "Update trainee's trainers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainers updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{username}/trainers")
    public List<TrainerInfoResponse> updateTraineeTrainers(@PathVariable String username, @Valid @RequestBody UpdateTraineeTrainersRequest request) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] PUT /trainees/{}/trainers", txId, username);
        try {
            List<String> usernames = request.getTrainers().stream()
                    .map(UpdateTraineeTrainersRequest.TrainerUsernameRequest::getUsername)
                    .toList();
            List<Trainer> trainers = traineeService.updateTraineeTrainers(username, usernames);
            List<TrainerInfoResponse> response = trainers.stream()
                    .map(t -> new TrainerInfoResponse(
                            t.getUsername(),
                            t.getFirstName(),
                            t.getLastName(),
                            t.getTrainingType()
                    )).toList();

            LOGGER.info("[{}] updateTraineeTrainers success - {} trainers assigned", txId, response.size());
            return response;
        } catch (Exception ex) {
            LOGGER.error("[{}] updateTraineeTrainers failed - {}", txId, ex.getMessage());
            throw ex;
        }
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

        try {
            TrainingSearchCriteria criteria = new TrainingSearchCriteria(periodFrom, periodTo, trainerUsername, username, trainingTypeId);
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
            LOGGER.info("[{}] getTraineeTrainings success - {} trainings", txId, response.size());
            return response;
        } catch (Exception ex) {
            LOGGER.error("[{}] getTraineeTrainings failed - {}", txId, ex.getMessage());
            throw ex;
        }
    }

    @Operation(summary = "Activate or deactivate trainee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activation updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("{username}/activation")
    public void updateTraineeActivation(
            @PathVariable String username,
            @Valid @RequestBody Boolean isActive) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] PATCH /trainees/{}/activation - isActive={}", txId, username, isActive);

        try {
            traineeService.updateActivationStatus(username, isActive);

            LOGGER.info("[{}] updateTraineeActivation success - 200 OK", txId);

        } catch (Exception ex) {
            LOGGER.error("[{}] updateTraineeActivation failed - {}", txId, ex.getMessage());
            throw ex;
        }
    }
}
