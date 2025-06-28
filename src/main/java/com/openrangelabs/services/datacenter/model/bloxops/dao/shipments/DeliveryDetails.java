package com.openrangelabs.services.datacenter.model.bloxops.dao.shipments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeliveryDetails {
    @JsonProperty("created-at")
    String created;
    @JsonProperty("image-url")
    String imageUrl;
    @JsonProperty("notes")
    String notes;
    @JsonProperty("recipient-name")
    String recipient;
    @JsonProperty("status")
    String status;
    @JsonProperty("tracking-number")
    String trackingNumber;
    @JsonProperty("updated-at")
    String updated;

}
