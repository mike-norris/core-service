package com.openrangelabs.services.operations.model;
import lombok.Data;

import java.util.List;

@Data
public class AlertsResponse {
    List<Alert> alerts;
    String error;
}
