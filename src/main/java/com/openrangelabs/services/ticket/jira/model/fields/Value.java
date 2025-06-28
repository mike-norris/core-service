package com.openrangelabs.services.ticket.jira.model.fields;

public class Value {
    String internal;

    public Boolean getInternal() {
        if (this.internal.equals("true")) {
            return true;
        }
        return false;
    }

    public void setInternal(String internal) { this.internal = internal; }

}
