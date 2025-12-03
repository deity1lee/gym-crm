package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.example.dto.*;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.example.exception.UnauthorizedException;
import org.example.service.AuthService;
import org.example.service.TraineeService;
import org.example.service.TrainingTypeService;
import org.example.utilities.TrainingSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

@Api(tags = "Trainees")
@RestController
@RequestMapping("/trainee")
public class TraineeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeController.class);

    private TraineeService traineeService;
    private AuthService authService;
    private TrainingTypeService trainingTypeService;

    public TraineeController(TraineeService traineeService, AuthService authService, TrainingTypeService trainingTypeService) {
        this.authService = authService;
        this.traineeService = traineeService;
        this.trainingTypeService = trainingTypeService;
    }

    @ApiOperation(value = "Register a new trainee",
            notes = "Creates a trainee profile and returns generated username and password")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Trainee successfully registered"),
            @ApiResponse(code = 400, message = "Validation or business error"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/profile")
    public ResponseEntity<UserCredentials> registerTrainee(@Valid @RequestBody TraineeRegistrationRequest request) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] POST /trainee/profile - registerTrainee({})", txId, requestSummary(request));
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
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            LOGGER.error("[{}] registerTrainee failed - 400 BAD REQUEST", txId);
            throw ex;
        }
    }

    @ApiOperation(value = "Get trainee profile by username",
            notes = "Returns a trainee profile based on provided username")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee profile retrieved successfully"),
            @ApiResponse(code = 400, message = "Validation or business error"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/profile")
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(@RequestHeader("X-Auth-Username") String username,
                                                                    @RequestHeader("X-Auth-Password") String password,
                                                                    @RequestParam String usernameForSearch) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] GET /trainee/profile - getTraineeProfile(usernameForSearch={})", txId, usernameForSearch);
        try {
            authService.authenticateUser(username, password);
            Trainee trainee = traineeService.selectTraineeByUsername(usernameForSearch);
            LOGGER.info("[{}] getTraineeProfile success - 200 OK - foundUsername={}", txId, trainee.getUsername());
            return ResponseEntity.ok(mapToResponse(trainee));
        } catch (UnauthorizedException ex) {
            LOGGER.error("[{}] getTraineeProfile failed - 401 UNAUTHORIZED - message={}", txId, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("[{}] getTraineeProfile failed - error={}", txId, ex.getMessage());
            throw ex;
        }
    }

    @ApiOperation("Update trainee profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Profile updated successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/profile")
    public ResponseEntity<TraineeProfileResponse> updateTraineeProfile(@RequestHeader("X-Auth-Username") String username,
                                                                       @RequestHeader("X-Auth-Password") String password,
                                                                       @Valid @RequestBody UpdateTraineeProfileRequest request) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] PUT /trainee/profile - updateTraineeProfile(usernameForUpdate={})", txId, request.getUsername());
        try {
            authService.authenticateUser(username, password);
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
            return ResponseEntity.ok(mapToResponse(updated));
        } catch (UnauthorizedException ex) {
            LOGGER.error("[{}] updateTraineeProfile failed - 401 UNAUTHORIZED - {}", txId, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("[{}] updateTraineeProfile failed - {}", txId, ex.getMessage());
            throw ex;
        }
    }

    @ApiOperation("Delete trainee profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Deleted successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteTrainee(@RequestHeader("X-Auth-Username") String username,
                                              @RequestHeader("X-Auth-Password") String password,
                                              @RequestParam String usernameForDelete) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] DELETE /trainee/profile - deleteTrainee(usernameForDelete={})", txId, usernameForDelete);
        try {
            authService.authenticateUser(username, password);
            traineeService.deleteTraineeByUsername(usernameForDelete);
            LOGGER.info("[{}] deleteTrainee success - 200 OK - deleted={}", txId, usernameForDelete);
            return ResponseEntity.ok().build();
        } catch (UnauthorizedException ex) {
            LOGGER.error("[{}] deleteTrainee failed - 401 UNAUTHORIZED - {}", txId, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("[{}] {}", txId, ex.getMessage());
            throw ex;
        }
    }

    @ApiOperation("Get active trainers not assigned to trainee")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Retrieved successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/not-assigned-trainers")
    public ResponseEntity<List<TrainerInfoResponse>> getNotAssignedActiveTrainers(@RequestHeader("X-Auth-Username") String username,
                                                                                  @RequestHeader("X-Auth-Password") String password,
                                                                                  @RequestParam String usernameForSearchingTrainers) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] GET /trainee/not-assigned-trainers - trainee={}", txId, usernameForSearchingTrainers);
        try {
            authService.authenticateUser(username, password);
            List<Trainer> trainers = traineeService.getUnassignedTrainers(usernameForSearchingTrainers);
            List<TrainerInfoResponse> response = trainers.stream()
                    .map(t -> new TrainerInfoResponse(
                            t.getUsername(),
                            t.getFirstName(),
                            t.getLastName(),
                            t.getTrainingType()
                    ))
                    .toList();
            LOGGER.info("[{}] getNotAssignedActiveTrainers success - {} trainers", txId, response.size());
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException ex) {
            LOGGER.error("[{}] getNotAssignedActiveTrainers failed - 401 UNAUTHORIZED - {}", txId, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("[{}] getNotAssignedActiveTrainers failed - {}", txId, ex.getMessage());
            throw ex;
        }
    }

    @ApiOperation("Update trainee's trainers")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainers updated successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/trainers")
    public ResponseEntity<List<TrainerInfoResponse>> updateTraineeTrainers(@RequestHeader("X-Auth-Username") String username,
                                                                           @RequestHeader("X-Auth-Password") String password,
                                                                           @Valid @RequestBody UpdateTraineeTrainersRequest request) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] PUT /trainee/trainers - trainee={}", txId, request.getTraineeUsername());
        try {
            authService.authenticateUser(username, password);

            List<String> usernames = request.getTrainers().stream()
                    .map(UpdateTraineeTrainersRequest.TrainerUsernameRequest::getUsername)
                    .toList();
            List<Trainer> trainers = traineeService.updateTraineeTrainers(request.getTraineeUsername(), usernames);
            List<TrainerInfoResponse> response = trainers.stream()
                    .map(t -> new TrainerInfoResponse(
                            t.getUsername(),
                            t.getFirstName(),
                            t.getLastName(),
                            t.getTrainingType()
                    )).toList();

            LOGGER.info("[{}] updateTraineeTrainers success - {} trainers assigned", txId, response.size());
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException ex) {
            LOGGER.error("[{}] updateTraineeTrainers failed - 401 UNAUTHORIZED - {}", txId, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("[{}] updateTraineeTrainers failed - {}", txId, ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/trainings")
    public ResponseEntity<List<TrainingInfoResponse>> getTraineeTrainings(
            @RequestHeader("X-Auth-Username") String username,
            @RequestHeader("X-Auth-Password") String password,
            @RequestParam String traineeUsername,
            @RequestParam(required = false) Date periodFrom,
            @RequestParam(required = false) Date periodTo,
            @RequestParam(required = false) String trainerUsername,
            @RequestParam(required = false) Long trainingTypeId) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] GET /trainee/trainings - trainee={}", txId, traineeUsername);

        try {
            authService.authenticateUser(username, password);

            TrainingSearchCriteria criteria = new TrainingSearchCriteria(periodFrom, periodTo, trainerUsername, traineeUsername, trainingTypeId);
            List<Training> trainings = traineeService.getTraineeTrainings(traineeUsername, criteria);
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
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException ex) {
            LOGGER.error("[{}] getTraineeTrainings failed - 401 - {}", txId, ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            LOGGER.error("[{}] getTraineeTrainings failed - {}", txId, ex.getMessage());
            throw ex;
        }
    }

    @ApiOperation("Activate or deactivate trainee")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Activation updated"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PatchMapping("/activation")
    public ResponseEntity<Void> updateTraineeActivation(@RequestHeader("X-Auth-Username") String username,
                                                        @RequestHeader("X-Auth-Password") String password,
                                                        @Valid @RequestBody ActivationRequest request) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] PATCH /trainee/activation - user={}", txId, request.getUsername());

        try {
            authService.authenticateUser(username, password);

            if (request.getUsername() == null || request.getUsername().isBlank()) {
                LOGGER.warn("[{}] Missing username in activation request", txId);
                throw new IllegalArgumentException("username is required");
            }
            if (request.getIsActive() == null) {
                LOGGER.warn("[{}] Missing isActive flag in request", txId);
                throw new IllegalArgumentException("isActive is required (true/false)");
            }
            traineeService.updateActivationStatus(request.getUsername(), request.getIsActive());

            LOGGER.info("[{}] updateTraineeActivation success - 200 OK", txId);
            return ResponseEntity.ok().build();

        } catch (UnauthorizedException ex) {
            LOGGER.error("[{}] updateTraineeActivation failed - 401 - {}", txId, ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            LOGGER.error("[{}] updateTraineeActivation failed - {}", txId, ex.getMessage());
            throw ex;
        }
    }

    private String requestSummary(TraineeRegistrationRequest request) {
        return String.format("firstName=%s, lastName=%s, dateOfBirth=%s, address=%s",
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress());
    }

    private TraineeProfileResponse mapToResponse(Trainee trainee) {
        TraineeProfileResponse response = new TraineeProfileResponse();

        response.setUsername(trainee.getUsername());
        response.setFirstName(trainee.getFirstName());
        response.setLastName(trainee.getLastName());
        response.setDateOfBirth(trainee.getDateOfBirth());
        response.setAddress(trainee.getAddress());
        response.setActive(trainee.isActive());

        List<TraineeProfileResponse.TrainerInfo> trainersInfo =
                trainee.getTrainers().stream()
                        .map(t -> new TraineeProfileResponse.TrainerInfo(
                                t.getUsername(),
                                t.getFirstName(),
                                t.getLastName(),
                                t.getTrainingType()
                        ))
                        .toList();

        response.setTrainers(trainersInfo);

        return response;
    }

}
