package com.openrangelabs.services.operations.dao.mappers;

import com.openrangelabs.services.operations.model.Alert;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AlertMapper implements RowMapper<Alert> {

    @Override
    public Alert mapRow(ResultSet rs, int rowNum) throws SQLException {
        Alert alert = new Alert();
        alert.setActive(rs.getBoolean("active"));
        alert.setDisplayed(rs.getString("displayed"));
        alert.setCreated_dt(rs.getString("created_dt"));
        alert.setMessage(rs.getString("message"));
        alert.setId(rs.getInt("id"));
        return alert;
    }

}
