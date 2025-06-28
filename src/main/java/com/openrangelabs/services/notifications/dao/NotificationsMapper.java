package com.openrangelabs.services.notifications.dao;

import com.openrangelabs.services.notifications.model.Notification;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationsMapper implements RowMapper<Notification> {

@Override
    public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
        Notification notification = new Notification();
        notification.setAccesstype(rs.getString("accesstype"));
        notification.setEventstatus(rs.getString("eventstatus"));
        notification.setEventtype(rs.getString("eventtype"));
        notification.setUserid(rs.getInt("userid"));
        notification.setRosteruserid(rs.getInt("rosteruserid"));
        notification.setCreated_dt(rs.getString("created_dt"));
        return notification;
        }

}
