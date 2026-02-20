package org.example.component.dto;

import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

@Component("componentResponseDto")
public class ResponseDto {

    private MvcResult result;

    public MvcResult getResult() {
        return result;
    }
    public void setResult(MvcResult result) {
        this.result = result;
    }
}
