package com.openrangelabs.services.signing.mappers;


import com.openrangelabs.services.signing.model.DocumentTemplate;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DocumentTemplateMapper implements RowMapper<DocumentTemplate> {
    @Override
    public DocumentTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
        DocumentTemplate documentTemplate = new DocumentTemplate();
        documentTemplate.setTemplateId(rs.getString("template_id"));
        documentTemplate.setName(rs.getString("name"));
        return documentTemplate;
    }
}

