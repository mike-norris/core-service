package com.openrangelabs.services.authenticate.model;

import lombok.Data;

@Data
public class NewUserPasswordChangeRequest {
    String key;
    String newpassword;
}
