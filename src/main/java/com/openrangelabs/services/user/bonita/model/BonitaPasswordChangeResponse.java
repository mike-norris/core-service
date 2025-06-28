package com.openrangelabs.services.user.bonita.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BonitaPasswordChangeResponse {
    @JsonProperty("ERROR")
    String error;
    @JsonProperty("SUCCESS")
    String success;

    public BonitaPasswordChangeResponse() {
    }

    public BonitaPasswordChangeResponse(String error, String success) {
        this.error = error;
        this.success = success;
    }
}
