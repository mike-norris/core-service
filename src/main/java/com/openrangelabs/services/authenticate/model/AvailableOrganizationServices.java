package com.openrangelabs.services.authenticate.model;

import lombok.Data;

@Data
public class AvailableOrganizationServices {
    long id;
    String name;
    String reference;
    long parent;
}
