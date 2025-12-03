package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.example.dto.*;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.example.exception.UnauthorizedException;
import org.example.service.AuthService;
import org.example.service.TrainerService;
import org.example.service.TrainingTypeService;
import org.example.utilities.TrainingSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "Trainers")
@RestController
@RequestMapping("/trainer")
public class TrainerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerController.class);

    private TrainerService trainerService;
    private AuthService authService;
    private TrainingTypeService trainingTypeService;

    public TrainerController(TrainerService trainerService, AuthService authService, TrainingTypeService trainingTypeService) {
        this.trainerService = trainerService;
        this.authService = authService;
        this.trainingTypeService = trainingTypeService;
    }

    @ApiOperation(value = "Register a new trainer", notes = "Creates a trainer profile and returns generated username and password")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Trainer successfully registered"),
            @ApiResponse(code = 400, message = "Validation or business error"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<UserCredentials> registerTrainer(@Valid @RequestBody TrainerRegistrationRequest request) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] POST /trainer/profile - registerTrainer({})", txId, requestSummary(request));
        try {
            Trainer trainer = new Trainer(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getTrainingType()
            );
            Trainer created = trainerService.createTrainer(trainer);
            UserCredentials response = new UserCredentials(
                    created.getUsername(),
                    created.getPassword()
            );
            LOGGER.info("[{}] registerTrainer success - 201 CREATED - username={}", txId, created.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            LOGGER.error("[{}] registerTrainer failed - 400 BAD REQUEST", txId);
            throw ex;
        }
    }

    @ApiOperation(
            value = "Get trainer profile by username",
            notes = "Returns a trainer profile for the specified username"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer profile retrieved successfully"),
            @ApiResponse(code = 400, message = "Validation or business error"),
            @ApiResponse(code = 401, message = "Unauthorized / invalid credentials"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/profile")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(@RequestHeader("X-Auth-Username") String username,
                                                                    @RequestHeader("X-Auth-Password") String password,
                                                                    @RequestParam String usernameForSearch) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] GET /trainer/profile - getTrainerProfile(requestedBy={}, search={})",
                txId, username, usernameForSearch);

        try {
            authService.authenticateUser(username, password);

            Trainer trainer = trainerService.selectTrainerByUsername(usernameForSearch);

            LOGGER.info("[{}] getTrainerProfile success - 200 OK - requested trainer={}", txId, usernameForSearch);
            return ResponseEntity.ok(mapToResponse(trainer));

        } catch (UnauthorizedException ex) {
            LOGGER.warn("[{}] getTrainerProfile failed - 401 UNAUTHORIZED - requestedBy={}", txId, username);
            throw ex;

        } catch (Exception ex) {
            LOGGER.error("[{}] getTrainerProfile failed - 400 BAD REQUEST - search={}", txId, usernameForSearch);
            throw ex;
        }
    }

    @ApiOperation(value = "Update trainer profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer profile updated successfully"),
            @ApiResponse(code = 400, message = "Validation or business error"),
            @ApiResponse(code = 401, message = "Unauthorized / invalid credentials"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/profile")
    public ResponseEntity<TrainerProfileResponse> updateTrainerProfile(@RequestHeader("X-Auth-Username") String username,
                                                                       @RequestHeader("X-Auth-Password") String password,
                                                                       @Valid @RequestBody UpdateTrainerProfileRequest request) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] PUT /trainer/profile - updateTrainerProfile(requestedBy={}, target={})",
                txId, username, request.getUsername());

        try {
            authService.authenticateUser(username, password);

            Trainer updatedTrainerData = new Trainer(
                    request.getUsername(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getTrainingType(),
                    request.getActive()
            );

            Trainer updatedTrainer = trainerService.updateTrainer(updatedTrainerData);

            LOGGER.info("[{}] updateTrainerProfile success - 200 OK - updated={}", txId, updatedTrainer.getUsername());
            return ResponseEntity.ok(mapToResponse(updatedTrainer));

        } catch (UnauthorizedException ex) {
            LOGGER.warn("[{}] updateTrainerProfile failed - 401 UNAUTHORIZED - requestedBy={}", txId, username);
            throw ex;

        } catch (Exception ex) {
            LOGGER.error("[{}] updateTrainerProfile failed - 400 BAD REQUEST - target={}", txId, request.getUsername());
            throw ex;
        }
    }

    @GetMapping("/trainings")
    public ResponseEntity<List<TrainingInfoResponse>> getTrainerTrainings(
            @RequestHeader("X-Auth-Username") String username,
            @RequestHeader("X-Auth-Password") String password,
            @RequestParam String trainerUsername,
            @RequestParam(required = false) Date periodFrom,
            @RequestParam(required = false) Date periodTo,
            @RequestParam(required = false) String traineeUsername) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] Get trainee trainings for trainee={}", txId, trainerUsername);

        authService.authenticateUser(username, password);

        TrainingSearchCriteria criteria = new TrainingSearchCriteria(periodFrom, periodTo, trainerUsername, traineeUsername, null);

        List<Training> trainings = trainerService.getTrainerTrainings(criteria);

        List<TrainingInfoResponse> response = new ArrayList<>();

        for (Training t : trainings) {
            response.add(new TrainingInfoResponse(
                    t.getName(),
                    t.getDate(),
                    new TrainingType(t.getTrainingType().getId(), t.getTrainingType().getTrainingTypeName()),
                    t.getDuration(),
                    t.getTrainer().getFirstName() + " " + t.getTrainer().getLastName()
            ));
        }

        return ResponseEntity.ok(response);
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
            trainerService.updateActivationStatus(request.getUsername(), request.getIsActive());

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
    private TrainerProfileResponse mapToResponse(Trainer trainer) {
        TrainerProfileResponse response = new TrainerProfileResponse();

        response.setFirstName(trainer.getFirstName());
        response.setLastName(trainer.getLastName());
        response.setTrainingType(trainer.getTrainingType());
        response.setActive(trainer.isActive());

        List<TrainerProfileResponse.TraineeInfo> traineesInfo =
                trainer.getTrainees().stream()
                        .map(t -> new TrainerProfileResponse.TraineeInfo(
                                t.getUsername(),
                                t.getFirstName(),
                                t.getLastName()
                        ))
                        .toList();

        response.setTrainees(traineesInfo);

        return response;
    }
    private String requestSummary(TrainerRegistrationRequest request) {
            return String.format("firstName=%s, lastName=%s, specialization=%s",
                    request.getFirstName(),
                    request.getLastName(),
                    request.getTrainingType().getTrainingTypeName());
        }
}
