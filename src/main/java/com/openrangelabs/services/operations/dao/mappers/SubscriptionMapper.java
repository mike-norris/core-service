package com.openrangelabs.services.operations.dao.mappers;

import com.openrangelabs.services.operations.model.Subscription;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class SubscriptionMapper implements RowMapper<Subscription> {

    @Override
    public Subscription mapRow(ResultSet rs, int rowNum) throws SQLException {
        Subscription subscription = new Subscription();
        subscription.setNotes(rs.getString("notes"));
        subscription.setName(rs.getString("name"));
        subscription.setExpiration_dt(rs.getString("expiration_dt"));
        subscription.setCreated_dt(rs.getString("created_dt"));
        subscription.setUsername(rs.getString("username"));
        subscription.setUrl(rs.getString("url"));
        subscription.setId(rs.getInt("id"));

        return subscription;
    }

}
