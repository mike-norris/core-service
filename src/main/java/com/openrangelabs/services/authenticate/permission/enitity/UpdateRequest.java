package com.openrangelabs.services.authenticate.permission.enitity;

import lombok.Data;

@Data
public class UpdateRequest {
    String componentName;
    String access;
    long pageId;
    long serviceId;
    long userId;
}
