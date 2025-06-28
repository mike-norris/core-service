package com.openrangelabs.services.roster.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RosterUserDatacenterRequest {
    @JsonProperty("rosteruser_id")
    Long rosteruserId;
    @JsonProperty("organization_id")
    Long organizationId;
    String datacenter;
    @JsonProperty("escort_required")
    boolean escortRequired;
    boolean vendor;
    boolean guest;
    int status;
}
