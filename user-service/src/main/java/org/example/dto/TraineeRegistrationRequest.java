package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Request body for trainee registration")
public class TraineeRegistrationRequest {

    @Schema(
            description = "First name of trainee",
            example = "John",
            required = true
    )
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(
            description = "Last name of trainee",
            example = "Doe",
            required = true
    )
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(
            description = "Date of birth of trainee (ISO format: yyyy-MM-dd)",
            example = "1998-05-20"
    )
    private LocalDate dateOfBirth;

    @Schema(
            description = "Address of trainee",
            example = "Some Street 123, City"
    )
    private String address;

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
}
