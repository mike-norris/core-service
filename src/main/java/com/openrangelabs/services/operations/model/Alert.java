package com.openrangelabs.services.operations.model;
import lombok.Data;

@Data
public class Alert {
    String message;
    String created_dt;
    boolean active;
    String displayed;
    int id;
}
