package com.openrangelabs.services.organization.model;

import com.openrangelabs.services.organization.entity.BadgeAccessPoint;
import lombok.Data;

import java.util.List;

@Data
public class BadgeAccessPointsResponse {

    List<BadgeAccessPoint> badgeAccessPointList;
    String error;

    public BadgeAccessPointsResponse(List<BadgeAccessPoint> badgeAccessPointList, String error) {
        this.badgeAccessPointList =badgeAccessPointList;
        this.error = error;
    }
    public BadgeAccessPointsResponse( String error) {
        this.error = error;
    }
}
