package com.openrangelabs.services.operations.dao.mappers;

import com.openrangelabs.services.operations.model.Timesheet;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TimesheetMapper implements RowMapper<Timesheet> {

    @Override
    public Timesheet mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timesheet timesheet = new Timesheet();
        timesheet.setId(rs.getInt("id"));
        timesheet.setEntry_date(rs.getString("entry_date"));
        timesheet.setAuthor_name(rs.getString("author_name"));
        timesheet.setHours(rs.getDouble("hours"));
        timesheet.setProject(rs.getString("project"));
        timesheet.setProject_class(rs.getString("project_class"));
        timesheet.setTask_class(rs.getString("task_class"));

        return timesheet;
    }
}
