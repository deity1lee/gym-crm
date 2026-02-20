package org.example.integration.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.example.integration.dto.ResponseDto;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponseValidationSteps {

    private final ResponseDto responseDto;

    public ResponseValidationSteps(ResponseDto responseDto) {
        this.responseDto = responseDto;
    }

    @Then("the response status should be {int}")
    public void responseStatusShouldBeInt(int status) {
        assertThat(responseDto.getResult().getStatusCode().value()).isEqualTo(status);
    }

    @And("a validation exception should be thrown with the message: {string}")
    public void aValidationExceptionShouldBeThrown(String exception) throws UnsupportedEncodingException {
        String responseBody = responseDto.getResult().getBody().toString();
        assertThat(responseBody).contains(exception);
    }
    @Then("the response should contain {string}")
    public void theResponseShouldContain(String expectedMessage) {
        assertThat(responseDto.getResult().getBody().toString())
                .contains(expectedMessage);
    }
}
