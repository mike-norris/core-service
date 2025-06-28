package com.openrangelabs.services.authenticate.permission.enitity;

import lombok.Data;

@Data
public class CompanyService {
    long id;
    long customerId;
    long datacenterId;
    String service;
    String serviceName;
    boolean isActive;
    boolean atl;
    boolean bmh;
    boolean cha;
    boolean hsv;
    boolean gsv;
}
