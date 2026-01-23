package org.example.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel("Update trainee profile request")
public class UpdateTraineeProfileRequest {

    @ApiModelProperty(required = true, example = "John.Doe36")
    @NotBlank
    private String username;

    @ApiModelProperty(required = true, example = "John")
    @NotBlank
    private String firstName;

    @ApiModelProperty(required = true, example = "Doe")
    @NotBlank
    private String lastName;

    @ApiModelProperty(example = "2010-03-11")
    private Date dateOfBirth;

    @ApiModelProperty(example = "NY")
    private String address;

    @ApiModelProperty(required = true, example = "false")
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
