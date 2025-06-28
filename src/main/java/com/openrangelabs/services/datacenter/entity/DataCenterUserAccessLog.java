package com.openrangelabs.services.datacenter.entity;

import lombok.Data;

/**
 * Dont use this class to push data into the table
 * Mike
 */

@Data
public class DataCenterUserAccessLog {

    String GUID;
    int partitionID;
    String messageDT;
    String personnelGUID;
    String personnelName;
    String accessPointGUID;
    String accessPointName;
    String status;
    String messageText;
    String messageType;
    String cardNumber;
    String dataCenter;
}
