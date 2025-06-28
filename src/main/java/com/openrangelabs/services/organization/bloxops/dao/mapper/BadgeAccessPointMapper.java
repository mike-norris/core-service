package com.openrangelabs.services.organization.bloxops.dao.mapper;

import com.openrangelabs.services.organization.entity.BadgeAccessPoint;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BadgeAccessPointMapper implements RowMapper<BadgeAccessPoint> {

    @Override
    public BadgeAccessPoint mapRow(ResultSet rs , int rowNum) throws SQLException {
        BadgeAccessPoint badgeAccessPoint = new BadgeAccessPoint();
        badgeAccessPoint.setBadgeId(rs.getInt("badge_id"));
        badgeAccessPoint.setId(rs.getInt("id"));
        badgeAccessPoint.setCommonName(rs.getString("common_name"));
        badgeAccessPoint.setMessageType(rs.getString("message_type"));
        badgeAccessPoint.setMessageDT(rs.getString("message_dt"));
        badgeAccessPoint.setAccess_point_guid(rs.getString("access_point_guid"));
        badgeAccessPoint.setName(rs.getString("name"));
        badgeAccessPoint.setDatacenter(rs.getString("datacenter"));

        return badgeAccessPoint;

    }
}
