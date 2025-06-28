package com.openrangelabs.services.signing.mappers;


import com.openrangelabs.services.signing.modelNew.DocumentInvite;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentInviteMapper implements RowMapper<DocumentInvite> {
    @Override
    public DocumentInvite mapRow(ResultSet rs, int rowNum) throws SQLException {
        DocumentInvite documentInvite = new DocumentInvite();
        documentInvite.setDocumentId(rs.getString("document_id"));
        documentInvite.setEmailAddress(rs.getString("email_address"));
        documentInvite.setArchived(rs.getBoolean("archived"));
        documentInvite.setTicketId(rs.getInt("ticket_id"));

        return documentInvite;
    }
}
