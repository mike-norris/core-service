package com.openrangelabs.services.datacenter.model.bloxops.dao.shipments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Delivery {
    String id;
    @JsonProperty("attributes")
    DeliveryDetails deliveryDetails;
    @JsonProperty("relationships")
    DeliveryRelationships deliveryRelationships;
}
