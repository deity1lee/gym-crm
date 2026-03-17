package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Update trainee profile request")
public class UpdateTraineeProfileRequest {

    @Schema(
            description = "Username of the trainee",
            example = "John.Doe36",
            required = true
    )
    @NotBlank
    private String username;

    @Schema(
            description = "First name of the trainee",
            example = "John",
            required = true
    )
    @NotBlank
    private String firstName;

    @Schema(
            description = "Last name of the trainee",
            example = "Doe",
            required = true
    )
    @NotBlank
    private String lastName;

    @Schema(
            description = "Date of birth of the trainee",
            example = "2010-03-11"
    )
    private LocalDate dateOfBirth;

    @Schema(
            description = "Address of the trainee",
            example = "NY"
    )
    private String address;

    @Schema(
            description = "Whether the trainee is active",
            example = "false",
            required = true
    )
    @NotNull
    private Boolean active;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
