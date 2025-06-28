package com.openrangelabs.services.ticket.jira.model;

public class StatusCategory {

    String self;
    String id;
    String key;
    String colorName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String name;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }
}
