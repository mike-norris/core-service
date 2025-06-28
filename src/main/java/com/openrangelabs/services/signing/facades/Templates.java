package com.openrangelabs.services.signing.facades;

public interface Templates {
    String createTemplate(String sourceDocumentId, String templateName) throws Exception;

    String createDocumentFromTemplate(String sourceTemplateId, String newDocumentName) throws Exception;
}
