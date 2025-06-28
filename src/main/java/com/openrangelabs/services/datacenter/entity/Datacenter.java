package com.openrangelabs.services.datacenter.entity;

import com.openrangelabs.services.roster.entity.UserAccess;
import lombok.Data;
import java.util.Set;

@Data
public class Datacenter {

    private long id;
    private String name;
    private String city;
    private String state;
    private String timezone;
    private Set<UserAccess> userAccess;
    private String managerEmailAddress;
    private String managerName;
    private String type;
}
