package org.example.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel("Change password request")
public class ChangePasswordRequest {

    @ApiModelProperty(required = true, example = "John.Do")
    @NotNull
    private String username;

    @ApiModelProperty(required = true, example = "pass123456")
    @NotBlank
    private String oldPassword;

    @ApiModelProperty(required = true, example = "A12e455Ier")
    @NotBlank
    private String newPassword;

    public String getUsername() {
        return username;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String username, String oldPassword, String newPassword) {
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}

