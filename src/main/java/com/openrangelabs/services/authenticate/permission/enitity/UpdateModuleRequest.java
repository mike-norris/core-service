package com.openrangelabs.services.authenticate.permission.enitity;

import lombok.Data;

@Data
public class UpdateModuleRequest {
    Long organizationId;
    Long serviceId;
    Boolean subPages;
    Boolean enabled;
    String access;
}
