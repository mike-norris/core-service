package com.openrangelabs.services.ticket.bloxops.mapper;

import com.openrangelabs.services.ticket.model.AttachmentHash;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AttachmentHashMapper implements RowMapper<AttachmentHash> {

    @Override
    public AttachmentHash mapRow(ResultSet rs, int rowNum) throws SQLException {
        AttachmentHash hash = new AttachmentHash();
        hash.setHash(rs.getString("hash"));
        hash.setOrganizationId(rs.getLong("organization_id"));
        hash.setObjectKey(rs.getString("object_key"));
        hash.setMimeType(rs.getString("mime_type"));
        hash.setFileName(rs.getString("filename"));
        return hash;
    }
}
