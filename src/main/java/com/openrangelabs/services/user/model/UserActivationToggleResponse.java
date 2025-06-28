package com.openrangelabs.services.user.model;

import lombok.Data;

@Data
public class UserActivationToggleResponse {
    boolean hasUpdated;
    String error;
    String string;

    public UserActivationToggleResponse(String error) {
        this.error = error;
    }

    public UserActivationToggleResponse(boolean hasUpdated) {
        this.hasUpdated = hasUpdated;
    }

    public UserActivationToggleResponse() {
    }

    public UserActivationToggleResponse(boolean hasUpdated, String error) {
        this.error = error;
        this.hasUpdated = hasUpdated;
    }

    public UserActivationToggleResponse(String string, String error) {
        this.string = string;
        this.error = error;
    }
}
