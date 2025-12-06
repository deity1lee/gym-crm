package org.example.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class UpdateTraineeTrainersRequest {

    @ApiModelProperty(required = true,  example = "[{ \"username\": \"John.Do\" }, { \"username\": \"Jane.yyDo\" }]")
    @NotEmpty
    private List<TrainerUsernameRequest> trainers;

    public List<TrainerUsernameRequest> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<TrainerUsernameRequest> trainers) {
        this.trainers = trainers;
    }

    public static class TrainerUsernameRequest {

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

