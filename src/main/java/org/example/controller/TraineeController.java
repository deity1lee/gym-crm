package org.example.controller;

import io.swagger.annotations.*;
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

import javax.validation.Valid;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

@Api(tags = "Trainees")
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

    @ApiOperation(
            value = "Register a new trainee",
            notes = "Creates a trainee profile and returns generated username and password"
    )
    @ApiResponses({
            @ApiResponse(code = 201, message = "Trainee successfully registered"),
            @ApiResponse(code = 400, message = "Validation or business error"),
            @ApiResponse(code = 500, message = "Internal server error")
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

    @ApiOperation(value = "Get trainee profile by username",
            notes = "Returns a trainee profile based on provided username")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee profile retrieved successfully"),
            @ApiResponse(code = 400, message = "Validation or business error"),
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

    @ApiOperation("Update trainee profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Profile updated successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
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

    @ApiOperation("Delete trainee profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Deleted successfully"),
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

    @ApiOperation("Get active trainers not assigned to trainee")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Retrieved successfully"),
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

    @ApiOperation("Update trainee's trainers")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainers updated successfully"),
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

    @ApiOperation("Get trainee's trainings")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee's trainings retrieved successfully"),
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
