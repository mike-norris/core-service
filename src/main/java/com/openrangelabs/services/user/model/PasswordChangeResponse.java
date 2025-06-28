package com.openrangelabs.services.user.model;
import lombok.Data;

@Data
public class PasswordChangeResponse {
    boolean success;
    String error;

    public PasswordChangeResponse(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public PasswordChangeResponse() {
    }
}
