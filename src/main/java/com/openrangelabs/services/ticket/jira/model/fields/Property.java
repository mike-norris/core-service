package com.openrangelabs.services.ticket.jira.model.fields;

public class Property {

    String key;
    Value value;

    public String getKey() { return this.key; }

    public void setKey(String key) {
        this.key = key;
    }

    public Value getValue() { return this.value; }

    public void setValue(Value value) {
        this.value = value;
    }

}