package com.openrangelabs.services.ticket.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TicketOutageDetails {
    @JsonProperty("created_date_time")
    String createdDateTime;
    List<TicketEquipment> equipment;
    @JsonProperty("post_mortem")
    String postMortem;
    @JsonProperty("output_recommendation")
    String outputRecommendation;

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public List<TicketEquipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<TicketEquipment> equipment) {
        this.equipment = equipment;
    }

    public String getPostMortem() {
        return postMortem;
    }

    public void setPostMortem(String postMortem) {
        this.postMortem = postMortem;
    }

    public String getOutputRecommendation() {
        return outputRecommendation;
    }

    public void setOutputRecommendation(String outputRecommendation) {
        this.outputRecommendation = outputRecommendation;
    }
}
