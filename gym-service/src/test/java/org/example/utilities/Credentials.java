package org.example.utilities;

import org.example.dto.UserCredentials;
import org.springframework.stereotype.Component;

@Component
@io.cucumber.spring.ScenarioScope
public class Credentials {

    private String jwt;

    private UserCredentials userCredentials;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public UserCredentials getUserCredentials() {
        return userCredentials;
    }

    public void setUserCredentials(UserCredentials userCredentials) {
        this.userCredentials = userCredentials;
    }
}
