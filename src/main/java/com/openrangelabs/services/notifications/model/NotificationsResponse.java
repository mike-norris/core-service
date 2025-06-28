package com.openrangelabs.services.notifications.model;

import lombok.Data;

import java.util.List;

@Data
public class NotificationsResponse {
    List<Notification> notifications;
    String error;

    public NotificationsResponse(List<Notification> notifications, String error) {
        this.notifications = notifications;
        this.error = error;
    }
}
