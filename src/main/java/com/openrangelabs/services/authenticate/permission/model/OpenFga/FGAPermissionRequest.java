package com.openrangelabs.services.authenticate.permission.model.OpenFga;

import lombok.Data;

@Data
public class FGAPermissionRequest {
    String user;
    String relation;
    String object;
}
