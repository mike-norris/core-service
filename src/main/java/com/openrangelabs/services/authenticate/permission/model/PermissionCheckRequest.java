package com.openrangelabs.services.authenticate.permission.model;

import lombok.Data;

@Data
public class PermissionCheckRequest {
    String pageName;
    String componentName;
    String userId;
    String orgId;
}
