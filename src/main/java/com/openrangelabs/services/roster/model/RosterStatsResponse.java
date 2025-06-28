package com.openrangelabs.services.roster.model;

import com.openrangelabs.services.roster.entity.RosterStats;

import java.util.List;

public class RosterStatsResponse {
    List<RosterStats> stats;
    String error;

    public List<RosterStats> getStats() {
        return stats;
    }

    public void setStats(List<RosterStats> stats) {
        this.stats = stats;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public RosterStatsResponse() {
    }

    public RosterStatsResponse(List<RosterStats> stats, String error) {
        this.stats = stats;
        this.error = error;
    }
}

