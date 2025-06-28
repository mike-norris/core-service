package com.openrangelabs.services.authenticate.permission.model.OpenFga;

import lombok.Data;

@Data
public class Tuple {
    String user;
    String relation;
    String object;
    String condition;
}
