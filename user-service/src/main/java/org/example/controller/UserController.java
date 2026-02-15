package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.ChangePasswordRequest;
import org.example.dto.TrainingCreationRequest;
import org.example.entities.TrainingType;
import org.example.orchestrator.TrainingOrchestratorService;
import org.example.service.AuthService;
import org.example.service.TrainingService;
import org.example.service.TrainingTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "Users")
@RestController
@RequestMapping
public class UserController {

    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;
    private final AuthService authService;
    private final TrainingOrchestratorService trainingOrchestratorService;

    public UserController(
            TrainingService trainingService,
            TrainingTypeService trainingTypeService,
            AuthService authService,
            TrainingOrchestratorService trainingOrchestratorService
    ) {
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
        this.authService = authService;
        this.trainingOrchestratorService = trainingOrchestratorService;
    }

    @Operation(summary = "Add a new training", description = "Creates a new training session for a trainee and trainer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Training successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation or business rule error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/trainings")
    public void addTraining(@Valid @RequestBody TrainingCreationRequest request) {
        trainingOrchestratorService.addTraining(
                request.getTraineeUsername(),
                request.getTrainerUsername(),
                request.getTrainingName(),
                trainingService.findTrainingTypeById(request.getTrainingTypeId()),
                request.getTrainingDate(),
                request.getTrainingDuration()
        );
    }

    @Operation(summary = "Delete training")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    @DeleteMapping("/trainings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTraining(@PathVariable Long id) {
        trainingOrchestratorService.deleteTraining(id);
    }

    @Operation(summary = "Get all training types", description = "Returns the list of available training types")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training types retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/training-types")
    public List<TrainingType> getTrainingTypes() {

        return trainingTypeService.getAllTrainingTypes();
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
    public String changePassword(
            @PathVariable String username,
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        authService.changePassword(
                username,
                request.getOldPassword(),
                request.getNewPassword()
        );
        return "Password changed successfully";
    }
}
