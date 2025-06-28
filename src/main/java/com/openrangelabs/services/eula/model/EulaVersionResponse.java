package com.openrangelabs.services.eula.model;

import lombok.Data;

@Data
public class EulaVersionResponse {
    String version;
    String updated;
    String error;

    public EulaVersionResponse(String error) {
        this.error = error;
    }
    public EulaVersionResponse(String version , String updated, String error) {
        this.version = version;
        this.updated = updated;
        this.error = error;
    }
}
