package com.openrangelabs.services.authenticate.model;

import lombok.Data;

@Data
public class Organization {
    Long id;
    Long fusebillId;
    Long parentId;
    String name;
    String identifier;
    String icon;
}
