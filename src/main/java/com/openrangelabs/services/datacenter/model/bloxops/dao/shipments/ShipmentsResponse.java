package com.openrangelabs.services.datacenter.model.bloxops.dao.shipments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ShipmentsResponse {
    @JsonProperty("data")
    List<Delivery> deliveries;
    @JsonProperty("included")
    List<DeliveryLocation> deliveryLocations;
    String error;
}
