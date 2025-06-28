package com.openrangelabs.services.message.model.slack;

import lombok.Data;

@Data
public class SlackErrorAlertResponse {
    boolean success;
    String error;
}
