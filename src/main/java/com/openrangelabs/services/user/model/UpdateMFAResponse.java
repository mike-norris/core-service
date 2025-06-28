package com.openrangelabs.services.user.model;

import lombok.Data;

@Data
public class UpdateMFAResponse {
    Boolean success;
    String error;

    public UpdateMFAResponse(boolean success, String error) {
        this.success = success;
        this.error = error;
    }
}
