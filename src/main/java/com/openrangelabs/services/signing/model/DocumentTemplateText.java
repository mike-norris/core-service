package com.openrangelabs.services.signing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.openrangelabs.services.organization.model.Organization;
import com.openrangelabs.services.roster.entity.RosterUser;
import com.openrangelabs.services.user.profile.model.UserProfile;
import lombok.Data;

import java.util.List;

@Data
public class DocumentTemplateText {
    String data;

    @JsonProperty("document_name")
    String documentName;

    @JsonProperty("texts")
    List<DocumentTemplateTextItems> texts;

    RosterUser rosterUser;
    UserProfile signer;
    UserProfile initiator;
    Organization organization;
}
