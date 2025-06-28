package com.openrangelabs.services.roster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RosterUserCreateResponse {
    String caseId;
    String error;

    public RosterUserCreateResponse(String error) {
        this.error = error;
    }

    public RosterUserCreateResponse() {

    }
}
