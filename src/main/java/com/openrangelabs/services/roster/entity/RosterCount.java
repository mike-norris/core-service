package com.openrangelabs.services.roster.entity;

import lombok.Data;

@Data
public class RosterCount {
    Long count;
    String datacenter;
    String city;
    String state;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getDatacenter() {
        return this.datacenter;
    }

    public void setDatacenter(String datacenter) {
        this.datacenter = datacenter;
    }

    public RosterCount() {
    }

    public RosterCount(Long count, String datacenter) {
        this.count = count;
        this.datacenter = datacenter;
    }
}

