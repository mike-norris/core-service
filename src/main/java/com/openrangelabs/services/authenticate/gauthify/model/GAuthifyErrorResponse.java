package com.openrangelabs.services.authenticate.gauthify.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GAuthifyErrorResponse {
    @JsonProperty("server_time")
    String serverTime;
    @JsonProperty("error_message")
    String errorMessage;
    @JsonProperty("error_code")
    String errorCode;
    @JsonProperty("http_status")
    String httpStatus;

    @Override
    public String toString() {
        return "GAuthifyErrorResponse{" +
                "serverTime='" + serverTime + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", httpStatus='" + httpStatus + '\'' +
                '}';
    }
}
