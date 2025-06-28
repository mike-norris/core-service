package com.openrangelabs.services.datacenter.model.bloxops.dao;

import com.openrangelabs.services.datacenter.entity.Datacenter;
import lombok.Data;

import java.util.List;

@Data
public class GetAllDCResponse {

    List<Datacenter> datacenters;
    String error;

    public GetAllDCResponse(List<Datacenter> datacenters, String error) {
        this.datacenters = datacenters;
        this.error = error;
    }
}
