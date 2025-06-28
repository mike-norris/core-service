package com.openrangelabs.services.signing.services;

import com.openrangelabs.services.signing.SNClient;
import com.openrangelabs.services.signing.dao.GenericId;
import com.openrangelabs.services.signing.dao.Template;
import com.openrangelabs.services.signing.exceptions.SNException;
import com.openrangelabs.services.signing.facades.Templates;

import java.util.Collections;

public class TemplatesService extends ApiService implements Templates {
    public TemplatesService(SNClient client) {
        super(client);
    }

    public String createTemplate(String sourceDocumentId, String templateName) throws SNException {
        return client.post(
                "/template",
                null,
                new Template.CreateRequest(templateName, sourceDocumentId),
                GenericId.class
        ).id;
    }

    public String createDocumentFromTemplate(String sourceTemplateId, String newDocumentName) throws SNException {
        return client.post(
                "/template/{sourceTemplateId}/copy",
                Collections.singletonMap("sourceTemplateId", sourceTemplateId),
                new Template.CopyRequest(newDocumentName),
                GenericId.class
        ).id;
    }
}
