package com.openrangelabs.services.notifications.model;

import lombok.Data;

@Data
public class Notification {
    String accesstype;
    String eventstatus;
    String eventtype;
    int userid;
    int rosteruserid;
    String created_dt;
}
