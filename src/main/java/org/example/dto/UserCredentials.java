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

    @Schema(
            description = "Generated jwt for trainee",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30"
    )
    private String token;

    public UserCredentials() {}

    public UserCredentials(String username, String password, String token) {
        this.username = username;
        this.password = password;
        this.token = token;
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
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
