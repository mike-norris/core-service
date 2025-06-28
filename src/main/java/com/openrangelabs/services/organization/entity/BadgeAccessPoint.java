package com.openrangelabs.services.organization.entity;

import lombok.Data;

@Data
public class BadgeAccessPoint {
    int badgeId;
    int id;
    String commonName;
    String messageType;
    String messageDT;
    String access_point_guid;
    String name;
    String datacenter;
}
