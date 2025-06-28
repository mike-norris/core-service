package com.openrangelabs.services.ticket.bloxops.mapper;

import com.openrangelabs.services.ticket.model.TicketType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketTypeMapper implements RowMapper<TicketType> {

    @Override
    public TicketType mapRow(ResultSet rs, int rowNum) throws SQLException {
        TicketType ticketType = new TicketType();
        ticketType.setType(rs.getString("type"));
        ticketType.setBonita(rs.getBoolean("bonita"));
        ticketType.setChangegear(rs.getBoolean("changegear"));

        return ticketType;
    }

}
