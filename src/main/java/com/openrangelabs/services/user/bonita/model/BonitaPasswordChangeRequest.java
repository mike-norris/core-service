package com.openrangelabs.services.user.bonita.model;
import lombok.Data;

@Data
public class BonitaPasswordChangeRequest {
    String username;
    String newpassword;

    public BonitaPasswordChangeRequest(String username, String newpassword) {
        this.username = username;
        this.newpassword = newpassword;
    }
}
