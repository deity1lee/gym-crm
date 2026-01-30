package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.ChangePasswordRequest;
import org.example.dto.TrainingCreationRequest;
import org.example.entities.TrainingType;
import org.example.service.AuthService;
import org.example.service.TrainingService;
import org.example.service.TrainingTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "Users")
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

    @Operation(summary = "Add a new training", description = "Creates a new training session for a trainee and trainer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Training successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation or business rule error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")

    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/trainings")
    public void addTraining(@Valid @RequestBody TrainingCreationRequest request) {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] POST /trainings - addTraining(trainee={}, trainer={}, typeId={})",
                txId, request.getTraineeUsername(), request.getTrainerUsername(), request.getTrainingTypeId());

        trainingService.addTraining(
                    request.getTraineeUsername(),
                    request.getTrainerUsername(),
                    request.getTrainingName(),
                    trainingService.findTrainingTypeById(request.getTrainingTypeId()),
                    request.getTrainingDate(),
                    request.getTrainingDuration()
            );

            LOGGER.info("[{}] addTraining success - 201 CREATED", txId);

    }

    @Operation(summary = "Get all training types", description = "Returns the list of available training types")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training types retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/training-types")
    public List<TrainingType> getTrainingTypes() {

        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] GET /training-types - fetch all training types", txId);

        try {
            List<TrainingType> types = trainingTypeService.getAllTrainingTypes();
            LOGGER.info("[{}] getTrainingTypes success - {} types found", txId, types.size());
            return types;

        } catch (Exception ex) {
            LOGGER.error("[{}] getTrainingTypes failed - {}", txId, ex.getMessage());
            throw ex;
        }
    }

    @Operation(
            summary = "Change user password",
            description = "Authenticates the user and updates the password"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Validation/business error"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/users/{username}/password")
    public String changePassword(@PathVariable String username, @Valid @RequestBody ChangePasswordRequest request) {

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
            return "Password changed successfully";
        } catch (Exception ex) {
            LOGGER.error("[{}] changePassword failed - 400 BAD REQUEST - username={}", txId, username);
            throw ex;
        }
    }
}
