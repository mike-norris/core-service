package com.openrangelabs.services.documents.mappers;

import com.openrangelabs.services.documents.entity.Document;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentMapper implements RowMapper<Document> {

    @Override
    public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
        Document document = new Document();
        document.setUserId(rs.getString("user_id"));
        document.setRosterId(rs.getString("roster_id"));
        document.setDocumentId(rs.getString("document_id"));
        document.setDocumentLocation(rs.getString("document_location"));
        document.setDeleted(rs.getBoolean("deleted"));
        document.setCreatedDt(rs.getString("created_dt"));
        document.setDocumentName(rs.getString("document_name"));
        document.setS3Container(rs.getString("s3_container"));
        document.setDocumentKey(rs.getString("document_key"));

        return document;
    }
}

