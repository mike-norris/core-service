package com.openrangelabs.services.roster.model;

import lombok.Data;

import java.util.List;

@Data
public class RosterSummaryResponse {
    List<RosterSummary> locationsSummary;
    int activeRosterCount;
    String error;

    public RosterSummaryResponse(List<RosterSummary> locationsSummary, int activeRosterCount) {
        this.locationsSummary = locationsSummary;
        this.activeRosterCount = activeRosterCount;
    }

    public RosterSummaryResponse(String error) {
        this.error = error;
    }
}
