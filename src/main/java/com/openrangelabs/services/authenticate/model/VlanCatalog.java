package com.openrangelabs.services.authenticate.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class VlanCatalog {
    Long id;
    Long vlan;
    Boolean availible;
    OffsetDateTime issueDatetime;
}
