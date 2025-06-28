package com.openrangelabs.services.ticket.metadefender.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ScanResponse {
    @JsonProperty("data_id")
    String dataId;
    String status;
    @JsonProperty("in_queue")
    String inQueue;
    @JsonProperty("queue_priority")
    String queuePriority;
    String sha1;
    String sha256;

    // local field
    String error;

    public ScanResponse(String error) {
        this.error = error;
    }

    public ScanResponse() {
    }
}
