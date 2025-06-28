package com.openrangelabs.services.roster.model;

import com.openrangelabs.services.roster.entity.RosterUser;

import java.util.List;

public class RosterUserResponse {
    public List<RosterUser> getRosterUsers() {
        return rosterUsers;
    }

    public void setRosterUsers(List<RosterUser> rosterUsers) {
        this.rosterUsers = rosterUsers;
    }

    List<RosterUser> rosterUsers;
    String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public RosterUserResponse() {
    }

    public RosterUserResponse(List<RosterUser> users, String error) {
        this.rosterUsers = users;
        this.error = error;
    }
}
