package com.openrangelabs.services.signing.mappers;

import com.openrangelabs.services.signing.model.DocumentTemplateTextItems;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class DocumentTemplateTextItemsMapper implements RowMapper<DocumentTemplateTextItems> {
    @Override
    public DocumentTemplateTextItems mapRow(ResultSet rs, int rowNum) throws SQLException {
        DocumentTemplateTextItems documentTemplateTextItems = new DocumentTemplateTextItems();
        documentTemplateTextItems.setDataType(rs.getString("data_type"));
        documentTemplateTextItems.setPageNumber(rs.getString("page_number"));
        documentTemplateTextItems.setX(rs.getString("x_coordinate"));
        documentTemplateTextItems.setY(rs.getString("y_coordinate"));
        documentTemplateTextItems.setLineHeight(rs.getString("line_height"));
        documentTemplateTextItems.setSize(rs.getString("font_size"));
        documentTemplateTextItems.setFont(rs.getString("font"));
        documentTemplateTextItems.setBold(rs.getBoolean("bold"));
        documentTemplateTextItems.setItalic(rs.getBoolean("italic"));
        documentTemplateTextItems.setUnderline(rs.getBoolean("underline"));
        return documentTemplateTextItems;
    }
}

