package com.openrangelabs.services.eula.model;

import lombok.Data;

@Data
public class EulaStatusUpdateRequest {
    String user_id;
    int eula_id;
    String status;
    String version;
    String browserInfo;
}
