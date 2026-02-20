package org.example.component.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.example.component.dto.ResponseDto;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponseValidationSteps {

    private final ResponseDto responseDto;

    public ResponseValidationSteps(ResponseDto responseDto) {
        this.responseDto = responseDto;
    }

    @Then("the response status should be {int}")
    public void responseStatusShouldBeInt(int status) {
        assertThat(responseDto.getResult().getResponse().getStatus()).isEqualTo(status);
    }

    @And("a validation exception should be thrown with the message: {string}")
    public void aValidationExceptionShouldBeThrown(String exception) throws UnsupportedEncodingException {
        String responseBody = responseDto.getResult().getResponse().getContentAsString();
        assertThat(responseBody).contains(exception);
    }

    @And("response message should be {string}")
    public void responseMessageShouldBe(String message) throws UnsupportedEncodingException {
        assertThat(responseDto.getResult().getResponse().getContentAsString()).isEqualTo(message);
    }
}
