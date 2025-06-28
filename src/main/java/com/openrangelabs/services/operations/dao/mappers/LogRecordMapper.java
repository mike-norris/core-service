package com.openrangelabs.services.operations.dao.mappers;

import com.openrangelabs.services.log.model.LogRecord;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class LogRecordMapper implements RowMapper<LogRecord> {

    @Override
    public LogRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        LogRecord logRecord = new LogRecord();
        logRecord.setCreated_dt(rs.getString("created_dt"));
        logRecord.setUser_id(rs.getInt("user_id"));
        logRecord.setOrganization_id(rs.getInt("organization_id"));
        logRecord.setDescription(rs.getString("description"));
        logRecord.setType(rs.getString("type"));
        return logRecord;
    }

}
