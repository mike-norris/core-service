package com.openrangelabs.services.ticket.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TicketNotification {
    @JsonProperty("created_date_time")
    String createdDateTime;
    @JsonProperty("notification_method")
    String notificationMethod;
    String destination;
    @JsonProperty("notification_interval")
    long notificationInterval;

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(String notificationMethod) {
        this.notificationMethod = notificationMethod;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public long getNotificationInterval() {
        return notificationInterval;
    }

    public void setNotificationInterval(long notificationInterval) {
        this.notificationInterval = notificationInterval;
    }
}
