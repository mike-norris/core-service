package com.openrangelabs.services.datacenter.model.bloxops.dao;

import com.openrangelabs.services.datacenter.entity.DataCenterUserAccessLog;
import lombok.Data;

import java.util.List;


@Data
public class DatacenterAccessResponse {
    List<DataCenterUserAccessLog> dataCenterUserAccessLogs;
    String error;

    public DatacenterAccessResponse(List<DataCenterUserAccessLog> dataCenterUserAccessLogs) {
        this.dataCenterUserAccessLogs = dataCenterUserAccessLogs;
    }
    public DatacenterAccessResponse(String error) {
        this.error = error;
    }
}
