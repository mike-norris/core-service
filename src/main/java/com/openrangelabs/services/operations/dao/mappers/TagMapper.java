package com.openrangelabs.services.operations.dao.mappers;

import lombok.Data;
import com.openrangelabs.services.operations.model.Tag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class TagMapper implements RowMapper<Tag> {

    @Override
    public Tag mapRow(ResultSet rs, int rowNum)throws SQLException {
        Tag tag = new Tag();
        tag.setTerm(rs.getString("term"));
        tag.setLinkId(rs.getInt("link_id"));
        return tag;
        }
}
