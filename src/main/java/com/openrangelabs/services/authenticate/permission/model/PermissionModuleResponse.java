package com.openrangelabs.services.authenticate.permission.model;

import com.openrangelabs.services.authenticate.permission.enitity.UIModule;
import lombok.Data;

import java.util.List;

@Data
public class PermissionModuleResponse {
    List<UIModule> uiModules;
    String error;

    public PermissionModuleResponse(List<UIModule> uiModules) {
        this.uiModules = uiModules;
    }

    public PermissionModuleResponse() {
    }
}
