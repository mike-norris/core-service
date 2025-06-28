package com.openrangelabs.services.authenticate.model;
import lombok.Data;

@Data
public class LoginRequest {
    String userName;
    String password;
    int attemptCount;
    IpDetails ipDetails;

    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public LoginRequest() {
    }
}
