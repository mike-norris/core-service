package com.openrangelabs.services.authenticate.permission.enitity;

import lombok.Data;

@Data
public class PermissionShort {
    String module;
    String name;
    String description;
    String page;
}
