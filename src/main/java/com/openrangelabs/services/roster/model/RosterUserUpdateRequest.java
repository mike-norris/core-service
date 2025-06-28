package com.openrangelabs.services.roster.model;

import com.openrangelabs.services.roster.entity.RosterUser;
import lombok.Data;

@Data
public class RosterUserUpdateRequest {
    RosterUser rosterUser;
    String token;
}
