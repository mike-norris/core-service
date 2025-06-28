package com.openrangelabs.services.roster.bloxops.dao.mapper;

import com.openrangelabs.services.roster.entity.RosterUserPhoto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class RosterUserPhotoMapper implements RowMapper<RosterUserPhoto> {

    @Override
    public RosterUserPhoto mapRow(ResultSet rs, int rowNum) throws SQLException {
        RosterUserPhoto rosterUserPhoto = new RosterUserPhoto();
        rosterUserPhoto.setRosteruserId(rs.getLong("rosteruser_id"));
        rosterUserPhoto.setBadgeId(rs.getLong("badge_id"));
        rosterUserPhoto.setPhotoLocation(rs.getString("photo_location"));
        rosterUserPhoto.setPhotoName(rs.getString("photo_name"));
        rosterUserPhoto.setStatus(rs.getInt("status"));
        rosterUserPhoto.setCreatedBy(rs.getInt("created_by"));
        rosterUserPhoto.setCreatedDt(rs.getObject("created_dt", OffsetDateTime.class));

        return rosterUserPhoto;
    }
}
