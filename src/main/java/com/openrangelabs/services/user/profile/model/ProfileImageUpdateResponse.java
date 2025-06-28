package com.openrangelabs.services.user.profile.model;
import lombok.Data;

@Data
public class ProfileImageUpdateResponse {
    boolean isUpdated;
    String error;

    public ProfileImageUpdateResponse() {
    }

    public ProfileImageUpdateResponse(boolean isUpdated) {
        this.isUpdated = isUpdated;
    }

    public ProfileImageUpdateResponse(boolean isUpdated, String error) {
        this.isUpdated = isUpdated;
        this.error = error;
    }
}
