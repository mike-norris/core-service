package com.openrangelabs.services.authenticate.permission.enitity;

import lombok.Data;

@Data
public class UserModule {
    long id;
    long userId;
    long customerId;
    long moduleId;
    long datacenterId;
    boolean isActive;
}
