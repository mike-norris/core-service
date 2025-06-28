package com.openrangelabs.services.datacenter.bloxops.dao.mapper;

import com.openrangelabs.services.datacenter.entity.DatacenterCalendar;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatacenterCalendarMapper implements RowMapper<DatacenterCalendar> {

    @Override
    public DatacenterCalendar mapRow(ResultSet rs, int rowNum) throws SQLException {
        DatacenterCalendar datacenterCalendar = new DatacenterCalendar();
        datacenterCalendar.setDatacenterId(rs.getInt("datacenter_id"));
        datacenterCalendar.setCalendarReference(rs.getString("calendar_reference"));
        datacenterCalendar.setGroupReference(rs.getString("group_reference"));
        datacenterCalendar.setTenantId(rs.getString("tenant_id"));
        datacenterCalendar.setName(rs.getString("name"));
        datacenterCalendar.setClientId(rs.getString("client_key"));
        datacenterCalendar.setClientSecret(rs.getString("client_secret"));

        return datacenterCalendar;
    }
}
