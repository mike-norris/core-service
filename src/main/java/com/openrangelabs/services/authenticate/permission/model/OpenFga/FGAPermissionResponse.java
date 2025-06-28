package com.openrangelabs.services.authenticate.permission.model.OpenFga;

import lombok.Data;

import java.util.List;

@Data
public class FGAPermissionResponse {
    boolean allowed;
    String error;
    List<TupleKey> tuples;
}
