package com.openrangelabs.services.ticket.jira.model.fields;

public class Watches {
    String self;
    Integer watchCount;
    Boolean isWatching;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public Integer getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(Integer watchCount) {
        this.watchCount = watchCount;
    }

    public Boolean getWatching() {
        return isWatching;
    }

    public void setWatching(Boolean watching) {
        isWatching = watching;
    }
}
