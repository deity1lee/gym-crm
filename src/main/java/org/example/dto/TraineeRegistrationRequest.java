package org.example.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@ApiModel(description = "Request body for trainee registration")
public class TraineeRegistrationRequest {

    @ApiModelProperty(value = "First name of trainee", required = true, example = "John")
    @NotBlank(message = "First name is required")
    private String firstName;

    @ApiModelProperty(value = "Last name of trainee", required = true, example = "Doe")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @ApiModelProperty(value = "Date of birth of trainee (ISO format: yyyy-MM-dd)", required = false, example = "1998-05-20")
    private Date dateOfBirth;

    @ApiModelProperty(value = "Address of trainee", required = false, example = "Some Street 123, City")
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

