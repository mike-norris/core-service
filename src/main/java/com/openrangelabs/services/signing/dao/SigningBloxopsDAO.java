package com.openrangelabs.services.signing.dao;

import com.openrangelabs.services.signing.mappers.DocumentInviteMapper;
import com.openrangelabs.services.signing.mappers.DocumentTemplateMapper;

import com.openrangelabs.services.signing.mappers.DocumentTemplateTextItemsMapper;
import com.openrangelabs.services.signing.model.DocumentTemplate;
import com.openrangelabs.services.signing.model.DocumentTemplateText;
import com.openrangelabs.services.signing.model.DocumentTemplateTextItems;
import com.openrangelabs.services.signing.modelNew.DocumentInvite;
import com.openrangelabs.services.user.bloxops.dao.mapper.CommunicationsMapper;
import com.openrangelabs.services.user.model.Communication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import static com.openrangelabs.services.config.BloxopsDBConfig.BLOXOPS_NAMEDJDBCTEMPLATE;

@Slf4j
@Repository
public class SigningBloxopsDAO {

    @Autowired
    @Qualifier(BLOXOPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;

    public void saveDocumentInvite(String documentId, String email, int ticketId) {
        String sql = "INSERT INTO document_invites (document_id,email_address ,ticket_id)" +
                "VALUES (:documentId, :email , :ticketId)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("documentId", documentId);
        params.addValue("ticketId", ticketId);
        params.addValue("email", email);
        jdbcTemplate.update(sql, params);
    }
    public List<DocumentInvite> findDocumentInvites(String email)  {
        String sql = "select email_address, document_id, archived ,ticket_id " +
                " from document_invites" +
                " where email_address = :email ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        return jdbcTemplate.query(sql, params, new DocumentInviteMapper());
    }

    public List<DocumentInvite> findAllDocumentInvites() {
        String sql = "select email_address, document_id, archived ,ticket_id " +
                " from document_invites" +
                " where archived IS NULL ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbcTemplate.query(sql, params, new DocumentInviteMapper());
    }

    public List<DocumentInvite> findAllDocumentInvitesWithTickets() {
        String sql = "select email_address, document_id, archived , ticket_id " +
                " from document_invites" +
                " where archived IS NULL AND ticket_id IS NOT NULL AND ticket_id != 0; ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbcTemplate.query(sql, params, new DocumentInviteMapper());
    }

    public int updateDocumentInvite(String documentId ,Boolean archivedUpdate) {
        String sql = "UPDATE document_invites SET archived=:archivedUpdate" +
                " WHERE document_id=:documentId;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("documentId", documentId);
        params.addValue("archivedUpdate", archivedUpdate);

        return jdbcTemplate.update(sql, params);
    }

    public DocumentTemplate getTemplateIdByName(String templateName) {
        String sql = "select * " +
                " from document_templates" +
                " where name = :templateName ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("templateName", templateName);
        return jdbcTemplate.query(sql, params, new DocumentTemplateMapper()).get(0);
    }

    public DocumentTemplateText getDocumentTemplateText(String templateId) {
        DocumentTemplateText dtt = new DocumentTemplateText();
        String sql = "select template_id, data_type, page_number, " +
                "x_coordinate, y_coordinate, line_height, " +
                "font_size, font, bold, italic, underline " +
                "from document_template_texts " +
                "where template_id = :templateId;";
        log.info("Get document texts sql: "+sql);
        log.info("Get document texts for template_id: "+templateId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("templateId", templateId);
        try {
            List<DocumentTemplateTextItems> texts = jdbcTemplate.query(sql, params, new DocumentTemplateTextItemsMapper());
            log.debug("Template texts: "+ Arrays.toString(new List[]{texts}));
            dtt.setTexts(texts);
        } catch (Exception e) {
            log.warn("Template row process error: "+e.getMessage());
            log.error(e.getMessage());
        }
        log.debug("Template texts list: "+dtt.getTexts().toString());
        return dtt;
    }

    public Communication getCommunication(String process) {
        String sql = "select * " +
                " from communications" +
                " where process_name = :process ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("process", process);
        return jdbcTemplate.query(sql, params, new CommunicationsMapper()).get(0);

    }
}
