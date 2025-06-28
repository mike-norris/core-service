package com.openrangelabs.services.user.model;

import lombok.Data;

@Data
public class UserCreateResponse {
    String caseId;
    String error;

    public UserCreateResponse(String error) {
        this.error = error;
    }

    public UserCreateResponse() {
    }
}
