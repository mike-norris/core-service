package com.openrangelabs.services.user.bonita.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BonitaUserCustomDetailDefinition {
    @JsonProperty("name")
    String name;
    @JsonProperty("description")
    String description;
    @JsonProperty("id")
    String id;
}
