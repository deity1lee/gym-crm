package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "Change password request")
public class ChangePasswordRequest {

    @Schema(required = true, example = "pass123456")
    @NotBlank
    private String oldPassword;

    @Schema(required = true, example = "A12e455Ier")
    @NotBlank
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}

