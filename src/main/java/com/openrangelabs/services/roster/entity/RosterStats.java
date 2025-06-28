package com.openrangelabs.services.roster.entity;

import lombok.Data;

@Data
public class RosterStats {
    String datacenter;
    String city;
    String state;
    String phone;
    boolean online;
    String manager;
    String managerEmail;
    String managerPhone;
    Long rosterCount;
}
