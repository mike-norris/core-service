package com.openrangelabs.services.authenticate.gauthify.model;
import lombok.Data;

@Data
public class GAuthifyCreateUserRequest {
    String uniqueId;
    String displayName;
    String email;

    public GAuthifyCreateUserRequest() {

    }

    public GAuthifyCreateUserRequest(String uniqueId, String displayName, String email) {
        this.uniqueId = uniqueId;
        this.displayName = displayName;
        this.email = email;
    }
}
