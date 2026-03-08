package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Username, Password, Token")
public class UserCredentials {

    @Schema(
            description = "Generated username for trainee",
            example = "John.Doe"
    )
    private String username;

    @Schema(
            description = "Generated password for trainee",
            example = "pass123456"
    )
    private String password;

    public UserCredentials() {}

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
