package com.openrangelabs.services.authenticate.permission.model;

import com.openrangelabs.services.authenticate.permission.enitity.UpdateRequest;
import lombok.Data;

import java.util.List;

@Data
public class UpdatePermissionsRequest {
  List<UpdateRequest> updates;
  String organizationId;
  String createdBy;
}
