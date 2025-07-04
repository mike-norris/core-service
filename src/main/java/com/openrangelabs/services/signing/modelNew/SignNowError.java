package com.openrangelabs.services.signing.modelNew;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)

@Data
public class SignNowError {
    @JsonProperty("code")
    Long code;

    @JsonProperty("message")
    String message;
}
