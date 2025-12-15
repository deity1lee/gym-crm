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
import org.example.utilities.JwtUtil;
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

@Tag(name = "Trainers")
@RestController
@RequestMapping("/trainers")
public class TrainerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerController.class);

    private final TrainerService trainerService;
    private final TrainerMapper mapper;
    private final JwtUtil jwtUtil;


    public TrainerController(TrainerService trainerService, TrainerMapper mapper, JwtUtil jwtUtil) {
        this.trainerService = trainerService;
        this.mapper = mapper;
        this.jwtUtil = jwtUtil;
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

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] POST /trainers - registerTrainer(firstName='{}', lastName='{}')", txId, request.getFirstName(), request.getLastName());
        try {
            Trainer trainer = new Trainer(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getTrainingType()
            );
            Trainer created = trainerService.createTrainer(trainer);
            String token = jwtUtil.generateToken(created.getUsername());
            UserCredentials response = new UserCredentials(
                    created.getUsername(),
                    created.getRawPassword(),
                    token
            );
            LOGGER.info("[{}] registerTrainer success - 201 CREATED - username={}", txId, created.getUsername());
            return response;
        } catch (Exception ex) {
            LOGGER.error("[{}] registerTrainer failed - 400 BAD REQUEST", txId);
            throw ex;
        }
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
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] GET /trainers/{} - getTrainerProfile(trainer='{}')", txId, username, username);
        try {
            Trainer trainer = trainerService.selectTrainerByUsername(username);

            LOGGER.info("[{}] getTrainerProfile success - 200 OK - requested trainer={}", txId, username);
            return mapper.toResponse(trainer);

        } catch (Exception ex) {
            LOGGER.error("[{}] getTrainerProfile failed - 400 BAD REQUEST - trainer={}", txId, username);
            throw ex;
        }
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

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] PUT /trainers - updateTrainerProfile(trainer='{}')", txId, request.getUsername());

        try {
            Trainer updatedTrainerData = new Trainer(
                    request.getUsername(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getTrainingType(),
                    request.getActive()
            );

            Trainer updatedTrainer = trainerService.updateTrainer(updatedTrainerData);

            LOGGER.info("[{}] updateTrainerProfile success - 200 OK - updated={}", txId, updatedTrainer.getUsername());
            return mapper.toResponse(updatedTrainer);

        } catch (Exception ex) {
            LOGGER.error("[{}] updateTrainerProfile failed - 400 BAD REQUEST - target={}", txId, request.getUsername());
            throw ex;
        }
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

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] GET trainers/{}/trainings - getTrainerTrainings(trainer='{}')", txId, username, username);

        TrainingSearchCriteria criteria = new TrainingSearchCriteria(periodFrom, periodTo, username, traineeUsername, null);

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
        return response;
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

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] PATCH /trainers/{}/activation - isActive={}", txId, username, isActive);

        try {
            trainerService.updateActivationStatus(username, isActive);

            LOGGER.info("[{}] updateTrainerActivation success - 200 OK", txId);

        } catch (Exception ex) {
            LOGGER.error("[{}] updateTrainerActivation failed - {}", txId, ex.getMessage());
            throw ex;
        }
    }
}
