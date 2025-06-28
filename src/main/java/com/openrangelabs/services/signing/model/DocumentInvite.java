package com.openrangelabs.services.signing.model;

import com.openrangelabs.services.signing.dao.Document;
import lombok.Data;

import java.util.List;

@Data
public class DocumentInvite {
    String documentId;
    String documentName;
    List<Document.FieldInvite> fieldInvites;
    String emailAddress;
    Boolean archived;
}
