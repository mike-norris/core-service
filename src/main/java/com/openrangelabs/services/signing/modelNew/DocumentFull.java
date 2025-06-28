package com.openrangelabs.services.signing.modelNew;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DocumentFull {

    String id;
    String parent_id;
    String user_id;
    String document_name;
    String page_count;
    String created;
    String updated;
    String original_filename;
    String original_document_id;
    String owner;
    String owner_name;
    Boolean template;
    String origin_user_id;
    String version_time;
    @JsonProperty("field_invites")
    List<FieldInvite> fieldInvites;
    @JsonProperty("fields")
    List<DocumentField> fields;
}
