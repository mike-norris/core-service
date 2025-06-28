package com.openrangelabs.services.user.model;

import lombok.Data;

@Data
public class UserActivationToggleRequest {
    String userId;
    String organizationId;
    boolean enabled;
    String role;
    String ticketId;
}
