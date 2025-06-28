package com.openrangelabs.services.authenticate.model;
import lombok.Data;

@Data
public class SessionInfo {

    String sessionToken;
    String sessionId;

    public SessionInfo(String sessionToken, String sessionId) {
        this.sessionToken = sessionToken;
        this.sessionId = sessionId;
    }

    public SessionInfo() {
    }
}
