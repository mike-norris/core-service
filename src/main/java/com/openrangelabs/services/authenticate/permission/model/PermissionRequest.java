package com.openrangelabs.services.authenticate.permission.model;

import lombok.Data;

@Data
public class PermissionRequest {
    long serviceId;
    long organizationId;
    long userId;
}
