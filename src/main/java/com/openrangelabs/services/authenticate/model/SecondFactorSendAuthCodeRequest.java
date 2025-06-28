package com.openrangelabs.services.authenticate.model;

import lombok.Data;

@Data
public class SecondFactorSendAuthCodeRequest {
    String userName;
    SecondFactorAuthType authType;
    int attemptCount;
    String sharedUserEmail;
}
