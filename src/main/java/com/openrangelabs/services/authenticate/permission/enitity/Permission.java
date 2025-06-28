package com.openrangelabs.services.authenticate.permission.enitity;

import lombok.Data;

@Data
public class Permission {
    long userId;
    long orgId;
    Boolean enabled;
    String access;

}
