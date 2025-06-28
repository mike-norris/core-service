package com.openrangelabs.services.datacenter.model.bloxops.dao.shipments;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeliveryRelationships {
    @JsonProperty("delivery-area")
    DeliveryArea deliveryArea;
}
