package com.openrangelabs.services.signing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DocumentDocumentGroup {
    @JsonProperty("document_group_id")
    public String groupId;

    @JsonProperty("document_group_name")
    public String groupName;

    @JsonProperty("invite_id")
    public String inviteId;

    @JsonProperty("invite_status")
    public String inviteStatus;
}
