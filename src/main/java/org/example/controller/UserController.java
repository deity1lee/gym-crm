package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.example.dao.UserDao;
import org.example.dto.ChangePasswordRequest;
import org.example.dto.TraineeRegistrationRequest;
import org.example.dto.TrainingCreationRequest;
import org.example.dto.UserCredentials;
import org.example.entities.Trainee;
import org.example.entities.TrainingType;
import org.example.exception.UnauthorizedException;
import org.example.service.AuthService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.example.service.TrainingTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Users")
@RestController
@RequestMapping
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private TrainingService trainingService;
    private TrainingTypeService trainingTypeService;
    private AuthService authService;

    public UserController(TrainingService trainingService, TrainingTypeService trainingTypeService, AuthService authService) {
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
        this.authService = authService;
    }

    @ApiOperation(value = "Add a new training", notes = "Creates a new training session for a trainee and trainer")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Training successfully created"),
            @ApiResponse(code = 400, message = "Validation or business rule error"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("/trainings")
    public ResponseEntity<Void> addTraining(@Valid @RequestBody TrainingCreationRequest request) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] POST /trainings - addTraining(trainee={}, trainer={}, typeId={})",
                txId, request.getTraineeUsername(), request.getTrainerUsername(), request.getTrainingTypeId());

        try {
            trainingService.addTraining(
                    request.getTraineeUsername(),
                    request.getTrainerUsername(),
                    request.getTrainingName(),
                    trainingService.findTrainingTypeById(request.getTrainingTypeId()),
                    request.getTrainingDate(),
                    request.getTrainingDuration()
            );

            LOGGER.info("[{}] addTraining success - 201 CREATED", txId);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (Exception ex) {
            LOGGER.error("[{}] addTraining failed - 400 BAD REQUEST - {}", txId, ex.getMessage());
            throw ex;
        }
    }

    @ApiOperation(value = "Get all training types", notes = "Returns the list of available training types")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Training types retrieved successfully"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/training-types")
    public ResponseEntity<List<TrainingType>> getTrainingTypes() {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] GET /training-types - fetch all training types", txId);

        try {
            List<TrainingType> types = trainingTypeService.getAllTrainingTypes();
            LOGGER.info("[{}] getTrainingTypes success - {} types found", txId, types.size());
            return ResponseEntity.ok(types);

        } catch (Exception ex) {
            LOGGER.error("[{}] getTrainingTypes failed - {}", txId, ex.getMessage());
            throw ex;
        }
    }

    @ApiOperation(
            value = "Change user password",
            notes = "Authenticates the user and updates the password"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Password changed successfully"),
            @ApiResponse(code = 400, message = "Validation/business error"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping("/users/{username}/password")
    public ResponseEntity<String> changePassword(@PathVariable String username, @Valid @RequestBody ChangePasswordRequest request) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] PUT /users/{}/password - changePassword(username={})", txId, username, username);

        try {
            LOGGER.debug("[{}] Trying to change password of user {}", txId, username);

            authService.changePassword(
                    username,
                    request.getOldPassword(),
                    request.getNewPassword()
            );

            LOGGER.info("[{}] changePassword success - 200 OK - username={}", txId, username);
            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception ex) {
            LOGGER.error("[{}] changePassword failed - 400 BAD REQUEST - username={}", txId, username);
            throw ex;
        }
    }
}
