package com.openrangelabs.services.ticket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CGTicket {

    @JsonProperty("Summary")
    String Summary;
    @JsonProperty("Description")
    String Description;
    @JsonProperty("eTag")
    String eTag;
    @JsonProperty("ID")
    String ID;
    @JsonProperty("EndState")
    boolean EndState;

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public boolean isEndState() {
        return EndState;
    }

    public void setEndState(boolean endState) {
        EndState = endState;
    }

    public String getItemIDPIT() {
        return itemIDPIT;
    }

    public void setItemIDPIT(String itemIDPIT) {
        this.itemIDPIT = itemIDPIT;
    }

    public String getImpact() {
        return Impact;
    }

    public void setImpact(String impact) {
        Impact = impact;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(String priority) {
        Priority = priority;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getExternalItemID() {
        return ExternalItemID;
    }

    public void setExternalItemID(String externalItemID) {
        ExternalItemID = externalItemID;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }

    public String getSubmitDate() {
        return SubmitDate;
    }

    public void setSubmitDate(String submitDate) {
        SubmitDate = submitDate;
    }

    public String getCreatedDateTime() {
        return CreatedDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        CreatedDateTime = createdDateTime;
    }

    public int getItemOwner() {
        return ItemOwner;
    }

    public void setItemOwner(int itemOwner) {
        ItemOwner = itemOwner;
    }

    public int getAccessLocation() {
        return AccessLocation;
    }

    public void setAccessLocation(int accessLocation) {
        AccessLocation = accessLocation;
    }

    public List<CGComment> getHistoryRecords() {
        return HistoryRecords;
    }

    public void setHistoryRecords(List<CGComment> historyRecords) {
        HistoryRecords = historyRecords;
    }

    @JsonProperty("ItemIDPIT")
    String itemIDPIT;
    @JsonProperty("Impact")
    String Impact;
    @JsonProperty("Priority")
    String Priority;
    @JsonProperty("Status")
    String Status;
    @JsonProperty("ExternalItemID")
    String ExternalItemID;
    @JsonProperty("DueDate")
    String DueDate;
    @JsonProperty("SubmitDate")
    String SubmitDate;
    @JsonProperty("CreatedDateTime")
    String CreatedDateTime;
    @JsonProperty("ItemOwner")
    int ItemOwner;
    @JsonProperty("AccessLocation")
    int AccessLocation;
    @JsonProperty("HistoryRecords")
    List<CGComment> HistoryRecords;

}
