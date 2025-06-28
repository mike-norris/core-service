package com.openrangelabs.services.signing.modelNew;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DocumentShort {
    @JsonProperty("id")
    String documentId;
    String user_id;
    @JsonProperty("document_name")
    String documentName;
    Boolean pinned;
    String page_count;
    String created;
    String updated;
    String original_filename;
}
