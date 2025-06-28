package com.openrangelabs.services.user.model;
import lombok.Data;

@Data
public class PasswordChangeRequest {
    String passwordConfirm;
    String newPassword;
    boolean adminChange;
    String email;

    public PasswordChangeRequest() {
    }

    public PasswordChangeRequest(String passwordConfirm, String newPassword) {
        this.passwordConfirm = passwordConfirm;
        this.newPassword = newPassword;
    }
}
