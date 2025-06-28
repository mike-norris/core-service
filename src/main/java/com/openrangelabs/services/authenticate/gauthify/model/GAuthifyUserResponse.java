package com.openrangelabs.services.authenticate.gauthify.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GAuthifyUserResponse {
    @JsonProperty("server_time")
    String serverTime;
    @JsonProperty("user_count")
    String userCount;
    @JsonProperty("data")
    GAuthifyUser user;
    @JsonProperty("request_id")
    String requestId;

    GAuthifyErrorResponse error;
}
