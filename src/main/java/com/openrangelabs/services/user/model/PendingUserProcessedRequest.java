package com.openrangelabs.services.user.model;

import lombok.Data;

@Data
public class PendingUserProcessedRequest {
    String userId;
    String organizationId;
}
