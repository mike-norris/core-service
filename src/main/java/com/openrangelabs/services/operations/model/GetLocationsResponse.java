package com.openrangelabs.services.operations.model;

import com.openrangelabs.services.datacenter.entity.Datacenter;
import lombok.Data;

import java.util.List;

@Data
public class GetLocationsResponse {
    List<Datacenter> locations;
    String error;
}
