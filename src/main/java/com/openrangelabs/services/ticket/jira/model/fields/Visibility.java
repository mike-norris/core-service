package com.openrangelabs.services.ticket.jira.model.fields;

public class Visibility {

    String type;
    String value;

    public Visibility() {
        this.type = "";
        this.value = "";
    }

    public String getKey() { return this.type; }

    public void setKey(String type) {
        this.type = type;
    }

    public String getValue() { return this.value; }

    public void setValue(String value) {
        this.value = value;
    }
}
