package com.openrangelabs.services.log.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogRecord implements Serializable {
    String created_dt;
    int user_id;
    int organization_id;
    String description;
    String type;

    public LogRecord(int user_id ,int organization_id , String description  ,String type) {
        this.user_id = user_id;
        this.organization_id = organization_id;
        this.description = description;
        this.type = type;
    }

    public LogRecord() {

    }
}
