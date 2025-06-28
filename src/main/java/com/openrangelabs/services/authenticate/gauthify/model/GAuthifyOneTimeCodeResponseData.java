package com.openrangelabs.services.authenticate.gauthify.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GAuthifyOneTimeCodeResponseData {
    @JsonProperty("authenticated")
    boolean authenticated;

    public GAuthifyOneTimeCodeResponseData(){}

    public GAuthifyOneTimeCodeResponseData(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
