package com.openrangelabs.services.roster.model;

import com.openrangelabs.services.roster.entity.RosterBadge;
import lombok.Data;

import java.util.List;

@Data
public class GetBadgeDetailsResponse {
        List<RosterBadge> badges;
        String error;

        public GetBadgeDetailsResponse(String error) {
                this.error =error;
        }

        public GetBadgeDetailsResponse(List<RosterBadge> badges) {
                this.badges = badges;
        }
}
