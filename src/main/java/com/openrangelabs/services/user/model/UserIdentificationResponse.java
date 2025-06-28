package com.openrangelabs.services.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserIdentificationResponse {

    @JsonProperty("copyright")
    String copyright;
    @JsonProperty("user_id")
    String userId;
    @JsonProperty("user_name")
    String userName;
    @JsonProperty("session_id")
    String sessionId;
    @JsonProperty("conf")
    String conf;
    @JsonProperty("is_technical_user")
    String isTechincalUser;
    @JsonProperty("version")
    String version;
    String error;

    public UserIdentificationResponse() {
    }

    public UserIdentificationResponse(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
