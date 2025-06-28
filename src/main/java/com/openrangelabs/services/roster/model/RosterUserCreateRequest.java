package com.openrangelabs.services.roster.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RosterUserCreateRequest {

    @JsonProperty("ticketType")
    String ticketType;

    @JsonProperty("ticketID")
    String ticketID;

    @JsonProperty("firstname")
    String firstName;

    @JsonProperty("lastname")
    String lastName;

    @JsonProperty("isActive")
    Boolean isActive;

    @JsonProperty("userId")
    Long userId;

    @JsonProperty("createdBy")
    String createdBy;

    @JsonProperty("emailAddress")
    String emailAddress;

    @JsonProperty("organizationId")
    Long organizationId;

    @JsonProperty("datacenters")
    Set<RosterUserCreateDatacenterRequest> datacenters;

    @JsonProperty("badgeRequired")
    Boolean badgeRequired;

    @JsonProperty("positionTitle")
    String positionTitle;

    @JsonProperty("type")
    String type;
}
