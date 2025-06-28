package com.openrangelabs.services.authenticate.model;
import lombok.Data;

@Data
public class PasswordSession {
    String id;
    String userName;
    String url;
    boolean isExpired;

}
