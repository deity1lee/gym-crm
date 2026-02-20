package org.example.integration.dto;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

@Component("integrationResponseDto")
public class ResponseDto {

    private ResponseEntity<?> result;

    public ResponseEntity<?> getResult() {
        return result;
    }
    public void setResult(ResponseEntity<?> result) {
        this.result = result;
    }
}
