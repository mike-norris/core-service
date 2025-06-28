package com.openrangelabs.services.datacenter.model.bloxops.dao.shipments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeliveryLocationDetails {
    String name;
    boolean enabled;
    String address;
    @JsonProperty("total-deliveries-count")
    int deliveryCount;
    @JsonProperty("location-name")
    String locationName;

}
