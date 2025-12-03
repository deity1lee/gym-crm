package org.example.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.example.entities.TrainingType;

@ApiModel("Trainer brief info")
public class TrainerShortResponse {

    @ApiModelProperty
    private String username;

    @ApiModelProperty
    private String firstName;

    @ApiModelProperty
    private String lastName;

    @ApiModelProperty("Training type reference")
    private TrainingType specialization;

}

