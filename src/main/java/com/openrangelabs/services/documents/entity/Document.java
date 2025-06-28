package com.openrangelabs.services.documents.entity;

import lombok.Data;

@Data
public class Document {
    String createdDt;
    String documentLocation;
    String documentName;
    String userId;
    String rosterId;
    Boolean deleted;
    String s3Container;
    String documentId;
    String documentKey;

}
