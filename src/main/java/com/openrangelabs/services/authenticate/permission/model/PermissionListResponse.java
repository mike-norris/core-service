package com.openrangelabs.services.authenticate.permission.model;

import com.openrangelabs.services.authenticate.permission.enitity.PermissionShort;
import lombok.Data;

import java.util.List;

@Data
public class PermissionListResponse {
    List<PermissionShort> permissionList;
    String error;

    public PermissionListResponse(List<PermissionShort> permissionList, String error) {
        this.permissionList = permissionList;
        this.error = error;
    }

    public PermissionListResponse(String error) {
        this.error = error;
    }
}
