package com.openrangelabs.services.authenticate.permission.enitity;

import lombok.Data;

@Data
public class UIModule {
    long id;
    String refName;
    long serviceId;
    int orderNumber;
    String displayName;
}
