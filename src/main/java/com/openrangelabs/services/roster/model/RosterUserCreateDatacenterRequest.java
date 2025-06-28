package com.openrangelabs.services.roster.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RosterUserCreateDatacenterRequest {
    String datacenter;
    @JsonProperty("escort_required")
    String escortRequired;
    boolean vendor;
    boolean guest;
    int status;
}
