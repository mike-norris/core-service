package com.openrangelabs.services.signing.model;

import lombok.Data;

@Data
public class DocumentTemplate {
    int id;
    String name;
    String templateId;
    DocumentTemplateText documentText;
}
