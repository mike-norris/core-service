package com.openrangelabs.services.user.model;
import lombok.Data;

@Data
public class UpdateUserImageResponse {
    boolean updated;
    String error;

    public UpdateUserImageResponse(boolean updated, String error) {
        this.updated = updated;
        this.error = error;
    }
    public UpdateUserImageResponse() {
    }
}
