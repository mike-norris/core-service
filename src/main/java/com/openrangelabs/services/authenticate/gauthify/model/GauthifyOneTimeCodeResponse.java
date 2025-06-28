package com.openrangelabs.services.authenticate.gauthify.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GauthifyOneTimeCodeResponse {
    @JsonProperty("server_time")
    String serverTime;
    @JsonProperty("request_id")
    String requestId;
    @JsonProperty("data")
    GAuthifyOneTimeCodeResponseData data;
}
