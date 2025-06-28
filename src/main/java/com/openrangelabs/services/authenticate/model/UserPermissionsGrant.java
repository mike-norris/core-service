package com.openrangelabs.services.authenticate.model;
import lombok.Data;

@Data
public class UserPermissionsGrant {
    private Long id;
    private Integer userId;
    private Integer fusebillId;
    private Integer permissionId;
}
