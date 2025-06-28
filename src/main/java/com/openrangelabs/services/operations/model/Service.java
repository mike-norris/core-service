package com.openrangelabs.services.operations.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class Service {
    @JsonAlias("service_id")
    int serviceId;
    String name;
    String description;
    @JsonAlias("display_name")
    String displayName;
    @JsonAlias("parent_service_id")
    int parent_service_id;
}
