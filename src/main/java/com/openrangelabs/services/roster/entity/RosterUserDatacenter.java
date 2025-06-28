package com.openrangelabs.services.roster.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RosterUserDatacenter {

    @JsonProperty("rosteruser_id")
    private long rosteruserId;
    @JsonProperty("datacenter_id")
    private long datacenterId;
    private String datacenter;
    @JsonProperty("escort_required")
    private boolean escortRequired;
    private boolean vendor;
    private boolean guest;
    @JsonProperty("orl_employee")
    private boolean orlEmployee;
    private int status;
    @JsonProperty("created_by")
    private long createdBy;
    @JsonProperty("created_dt")
    private String createdDt;

}
