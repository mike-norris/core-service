package com.openrangelabs.services.authenticate.model;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class PasswordResetKey {

    String key;
    String userName;
    OffsetDateTime timestamp;
    boolean expired;

    public PasswordResetKey(String key, String userName, OffsetDateTime timestamp, boolean expired) {
        this.key = key;
        this.userName = userName;
        this.timestamp = timestamp;
        this.expired = expired;
    }

    public PasswordResetKey() {
    }
}
