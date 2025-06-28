package com.openrangelabs.services.signing.modelNew;

import lombok.Data;

import java.util.List;

@Data
public class DocumentInvite {
    String documentId;
    String documentName;
    int ticketId;
    List<FieldInvite> fieldInvites;
    List<DocumentField> fields;
    String emailAddress;
    Boolean archived;
}
