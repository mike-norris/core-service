package com.openrangelabs.services.authenticate.permission.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrganizationService {

    @JsonProperty("organizationId")
    @JsonAlias("organization_id")
    long organizationId;

    @JsonProperty("organizationName")
    @JsonAlias("organization_name")
    String organizationName;

    @JsonProperty("orgCode")
    @JsonAlias("org_code")
    String orgCode;

    String name;

    String role;

    Boolean beta;

    @JsonProperty("serviceId")
    @JsonAlias("service_id")
    long serviceId;

    @JsonProperty("displayName")
    @JsonAlias("display_name")
    String displayName;

    boolean active;

    @JsonProperty("datacenterId")
    @JsonAlias("datacenter_id")
    int datacenterId;

    @JsonProperty("datacenterName")
    @JsonAlias("datacenter_name")
    String datacenterName;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
