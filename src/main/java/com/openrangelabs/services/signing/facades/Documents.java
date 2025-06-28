package com.openrangelabs.services.signing.facades;

import com.openrangelabs.services.signing.dao.Document;
import com.openrangelabs.services.signing.exceptions.SNException;

import java.io.InputStream;
import java.util.List;

public interface Documents {
    String uploadDocument(InputStream stream, String fileName) throws SNException;

    String uploadDocumentWithTags(InputStream stream, String fileName) throws SNException;

    List<Document> getDocuments() throws SNException;

    Document.SigningLinkResponce createSigningLink(String documentId) throws SNException;

    void sendDocumentSignInvite(String documentId, Document.SigningInviteRequest request) throws SNException;

    void sendDocumentSignInvite(String documentId, Document.SigningInviteWithRolesRequest request) throws SNException;

    void updateDocumentFields(String documentId, List<Document.Field> request) throws SNException;

    Document getDocument(String documentId) throws SNException;

    void deleteDocument(String documentId) throws SNException;

    String getDownloadLink(String documentId) throws SNException;
}
