package com.openrangelabs.services.authenticate.bonita.model;

import lombok.Data;

@Data
public class BonitaCaseResponse {
    String caseId;
    String error;

    public BonitaCaseResponse() {
    }

    public BonitaCaseResponse(String error) {
        this.error = error;
    }
}
