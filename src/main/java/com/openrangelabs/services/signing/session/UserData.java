package com.openrangelabs.services.signing.session;


import com.openrangelabs.services.signing.model.DocumentInfo;
import com.openrangelabs.services.signing.SNClient;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserData {
    private SNClient snClient;
    private Set<DocumentInfo> userDocuments = new HashSet<>();

    public SNClient getSnClient() {
        return snClient;
    }

    public void setSnClient(SNClient snClient) {
        this.snClient = snClient;
    }

    public Set<DocumentInfo> getUserDocuments() {
        return Collections.unmodifiableSet(userDocuments);
    }

    public void setUserDocuments(Set<DocumentInfo> documents) {
        userDocuments.addAll(documents);
    }

    public void addDocument(DocumentInfo document) {
        this.userDocuments.add(document);
    }
}
