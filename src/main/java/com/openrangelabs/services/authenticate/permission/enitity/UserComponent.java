package com.openrangelabs.services.authenticate.permission.enitity;

import lombok.Data;

@Data
public class UserComponent {
    long id;
    long userId;
    long organizationId;
    long componentId;
    long datacenterId;
    String accessLevel;
}
