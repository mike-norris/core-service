package com.openrangelabs.services.signing.model;


import lombok.Data;

@Data
public class SigningResponse {
    Boolean success;
    String error;
    public SigningResponse(Boolean success ,String error) {
        this.success = success;
        this.error = error;
    }

    public SigningResponse(String error) {
        this.error = error;
    }

    public SigningResponse() {
    }
}
