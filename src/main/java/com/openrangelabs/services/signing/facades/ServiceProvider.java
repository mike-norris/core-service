package com.openrangelabs.services.signing.facades;

public interface ServiceProvider {
    Documents documentsService();

    Templates templatesService();

    DocumentGroups documentGroupsService();
}
