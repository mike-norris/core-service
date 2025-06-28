package com.openrangelabs.services.user.model;
import lombok.Data;

@Data
public class PasswordUrlResponse {
    String url;
    String error;

    public PasswordUrlResponse(String url, String error) {
        this.url = url;
        this.error = error;
    }

    public PasswordUrlResponse(String error) {
        this.error = error;
    }

    public PasswordUrlResponse() {
    }
}
