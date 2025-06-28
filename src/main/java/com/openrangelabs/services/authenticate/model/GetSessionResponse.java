package com.openrangelabs.services.authenticate.model;
import lombok.Data;

@Data
public class GetSessionResponse {
    Boolean hasSession;
    String error;

    public GetSessionResponse(boolean hasSession, String error) {
        this.hasSession = hasSession;
        this.error = error;
    }
}
