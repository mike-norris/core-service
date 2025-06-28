package com.openrangelabs.services.operations.dao.mappers;

import com.openrangelabs.services.operations.model.Link;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LinkMapper implements RowMapper<Link> {

    @Override
    public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
        Link link = new Link();
        link.setDepartment(rs.getString("department"));
        link.setUrl(rs.getString("url"));
        link.setId(rs.getInt("id"));
        link.setName(rs.getString("name"));
        link.setDescription(rs.getString("description"));
        link.setApproved(rs.getBoolean("approved"));
        return link;
    }
}
