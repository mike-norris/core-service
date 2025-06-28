package com.openrangelabs.services.authenticate.permission.model.OpenFga;

import lombok.Data;

@Data
public class CheckResponse {
    boolean allowed;
    String resolution;
}
