package com.openrangelabs.services.bonita.model;
import lombok.Data;

@Data
public class ProcessInitiationResponse {
    String caseId;
    String error;

    public ProcessInitiationResponse() {
    }

    public ProcessInitiationResponse(String error) {
        this.error = error;
    }
}
