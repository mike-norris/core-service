package com.openrangelabs.services.roster.entity;

import com.openrangelabs.services.datacenter.entity.DataCenterUserAccessLog;
import lombok.Data;

import java.util.List;

@Data
public class RosterBadge {
    int badgeId;
    int cardNumber;
    String badgeRef;
    Boolean active;
    int rosteruserId;
    int systemObjectId;
    int issuedBy;
    String issuedDt;
    boolean lost;
    String guid;
    String personnelGuid;
    String datacenter;
    String CommonName;
    boolean expired;
    boolean disabled;
    boolean stolen;
    boolean revoked;
    boolean disabledByInactivity;
    String printDate;
    String activationDt;
    String expirationDt;
    String locationTag;
    List<DataCenterUserAccessLog> dataCenterUserAccessLogList;
}
