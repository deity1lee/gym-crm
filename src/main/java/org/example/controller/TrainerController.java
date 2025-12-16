package org.example.controller;

import io.swagger.annotations.*;
import org.example.dto.*;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.example.mapper.TrainerMapper;
import org.example.service.TrainerService;
import org.example.utilities.TrainingSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "Trainers")
@RestController
@RequestMapping("/trainers")
public class TrainerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerController.class);

    private TrainerService trainerService;
    private TrainerMapper mapper;

    public TrainerController(TrainerService trainerService, TrainerMapper mapper) {
        this.trainerService = trainerService;
        this.mapper = mapper;
    }

    @ApiOperation(value = "Register a new trainer", notes = "Creates a trainer profile and returns generated username and password")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Trainer successfully registered"),
            @ApiResponse(code = 400, message = "Validation or business error"),
            @ApiResponse(code = 500, message = "Internal server error")
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
            UserCredentials response = new UserCredentials(
                    created.getUsername(),
                    created.getPassword()
            );
            LOGGER.info("[{}] registerTrainer success - 201 CREATED - username={}", txId, created.getUsername());
            return response;
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
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "X-Auth-Username",
                    value = "Username for authentication",
                    required = true,
                    paramType = "header"
            ),
            @ApiImplicitParam(
                    name = "X-Auth-Password",
                    value = "Password for authentication",
                    required = true,
                    paramType = "header"
            )
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

    @ApiOperation(value = "Update trainer profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer profile updated successfully"),
            @ApiResponse(code = 400, message = "Validation or business error"),
            @ApiResponse(code = 401, message = "Unauthorized / invalid credentials"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "X-Auth-Username",
                    value = "Username for authentication",
                    required = true,
                    paramType = "header"
            ),
            @ApiImplicitParam(
                    name = "X-Auth-Password",
                    value = "Password for authentication",
                    required = true,
                    paramType = "header"
            )
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

    @ApiOperation("Get trainer's trainings")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer's trainings retrieved successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "X-Auth-Username",
                    value = "Username for authentication",
                    required = true,
                    paramType = "header"
            ),
            @ApiImplicitParam(
                    name = "X-Auth-Password",
                    value = "Password for authentication",
                    required = true,
                    paramType = "header"
            )
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

    @ApiOperation("Activate or deactivate trainee")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Activation updated"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "X-Auth-Username",
                    value = "Username for authentication",
                    required = true,
                    paramType = "header"
            ),
            @ApiImplicitParam(
                    name = "X-Auth-Password",
                    value = "Password for authentication",
                    required = true,
                    paramType = "header"
            )
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
