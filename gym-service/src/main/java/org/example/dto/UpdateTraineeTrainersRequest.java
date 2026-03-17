package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Request for updating trainee's assigned trainers")
public class UpdateTraineeTrainersRequest {

    @Schema(
            description = "List of trainers assigned to the trainee",
            example = "[{ \"username\": \"John.Do\" }, { \"username\": \"Jane.yyDo\" }]",
            required = true
    )
    @NotEmpty
    private List<TrainerUsernameRequest> trainers;

    public List<TrainerUsernameRequest> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<TrainerUsernameRequest> trainers) {
        this.trainers = trainers;
    }

    public static class TrainerUsernameRequest {

        @Schema(
                description = "Trainer username",
                example = "John.Do",
                required = true
        )
        @NotBlank
        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
