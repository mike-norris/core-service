package com.openrangelabs.services.user.model;

import lombok.Data;

@Data
public class Communication {
    String processName;
    String system;
    String channel;
    Boolean internal;
}
