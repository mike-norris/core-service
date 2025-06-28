package com.openrangelabs.services.user.model;

import lombok.Data;

@Data
public class UserCustomImageResponse {
    boolean saved;
    String error;

    public UserCustomImageResponse(boolean saved, String error) {
        this.saved = saved;
        this.error = error;
    }
    public UserCustomImageResponse() {
    }
}
