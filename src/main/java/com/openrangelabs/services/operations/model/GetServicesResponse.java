package com.openrangelabs.services.operations.model;

import lombok.Data;

import java.util.List;

@Data
public class GetServicesResponse {
    List<Service> services;
    String error;
}
