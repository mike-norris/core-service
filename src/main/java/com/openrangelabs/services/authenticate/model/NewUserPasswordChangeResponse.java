package com.openrangelabs.services.authenticate.model;

import lombok.Data;

@Data
public class NewUserPasswordChangeResponse {
    boolean isSuccessful;
    String error;

    public NewUserPasswordChangeResponse(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public NewUserPasswordChangeResponse(boolean isSuccessful, String error) {
        this.isSuccessful = isSuccessful;
        this.error = error;
    }

    public NewUserPasswordChangeResponse() {
    }
}
