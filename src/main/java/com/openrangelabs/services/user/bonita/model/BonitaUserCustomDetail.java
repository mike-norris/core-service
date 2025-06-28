package com.openrangelabs.services.user.bonita.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BonitaUserCustomDetail {
    @JsonProperty("userId")
    String userId;
    @JsonProperty("value")
    String value;
    @JsonProperty("definitionId")
    BonitaUserCustomDetailDefinition definitionId;
}
