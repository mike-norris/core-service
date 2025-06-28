package com.openrangelabs.services.roster.model;

import lombok.Data;

import java.util.List;

@Data
public class RosterUserActivationToggleRequest {
    String userId;
    String organizationId;
    List<String> datacenter;
    boolean enabled;
}
