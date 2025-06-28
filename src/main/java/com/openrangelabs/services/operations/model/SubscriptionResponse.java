package com.openrangelabs.services.operations.model;

import lombok.Data;

import java.util.List;

@Data
public class SubscriptionResponse {
    List<Subscription> subscriptions;
    String error;
}
