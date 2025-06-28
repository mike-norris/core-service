package com.openrangelabs.services.user.model;

import lombok.Data;

@Data
public class UpdateMFARequest {
    String mfaMethod;
}
