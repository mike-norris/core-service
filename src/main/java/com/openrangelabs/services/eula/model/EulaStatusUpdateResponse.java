package com.openrangelabs.services.eula.model;

import lombok.Data;

@Data
public class EulaStatusUpdateResponse {
    Boolean updated;
    String error;

    public EulaStatusUpdateResponse(Boolean updated ,String error) {
        this.updated =updated;
        this.error = error;
    }
}
