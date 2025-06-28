package com.openrangelabs.services.user.model;

import lombok.Data;

@Data
public class UserRoleChangeRequest {
    String role;
    String ticketId;
    int orgId;
    int userId;
}
