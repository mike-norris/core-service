package com.openrangelabs.services.datacenter.model.bloxops.dao.shipments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeliveryLocation {
    String id;
    @JsonProperty("attributes")
    DeliveryLocationDetails deliveryLocationDetails;
}
