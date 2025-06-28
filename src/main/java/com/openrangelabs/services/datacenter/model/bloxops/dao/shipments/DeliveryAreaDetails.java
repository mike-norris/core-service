package com.openrangelabs.services.datacenter.model.bloxops.dao.shipments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeliveryAreaDetails {
    @JsonProperty("type")
    String type;
    @JsonProperty("id")
    String id;
}
