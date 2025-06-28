package com.openrangelabs.services.operations.jwt;

import lombok.Data;

@Data
public class JWTValidateResponse {
    Boolean isValid;
    String error;

    public JWTValidateResponse(Boolean tokenExpired) {
        this.isValid = !tokenExpired;
    }
}
