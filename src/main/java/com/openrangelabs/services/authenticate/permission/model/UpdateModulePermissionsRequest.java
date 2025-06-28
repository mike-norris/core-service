package com.openrangelabs.services.authenticate.permission.model;

import com.openrangelabs.services.authenticate.permission.enitity.UpdateModuleRequest;
import lombok.Data;

import java.util.List;

@Data
public class UpdateModulePermissionsRequest {
    List<UpdateModuleRequest> updates;
    String usersRole;
    String ticketId;
}
