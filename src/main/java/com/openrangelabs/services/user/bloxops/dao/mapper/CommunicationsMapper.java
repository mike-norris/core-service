package com.openrangelabs.services.user.bloxops.dao.mapper;

import com.openrangelabs.services.user.model.Communication;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommunicationsMapper implements RowMapper<Communication> {

    @Override
    public Communication mapRow(ResultSet rs, int rowNum) throws SQLException {
        Communication communication = new Communication();
        communication.setChannel(rs.getString("channel"));
        communication.setInternal(rs.getBoolean("internal"));
        communication.setSystem(rs.getString("system"));
        communication.setProcessName(rs.getString("process_name"));

        return communication;
    }
}
