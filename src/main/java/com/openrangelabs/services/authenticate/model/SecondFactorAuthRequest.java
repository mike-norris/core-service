package com.openrangelabs.services.authenticate.model;
import lombok.Data;

@Data
public class SecondFactorAuthRequest {
    String oneTimeCode;
    String userName;
    int attemptCount;
    SecondFactorAuthType authType;
    String sharedUserEmail;
    
}
