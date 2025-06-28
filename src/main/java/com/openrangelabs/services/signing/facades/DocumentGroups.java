package com.openrangelabs.services.signing.facades;

import com.openrangelabs.services.signing.dao.DocumentGroup;
import com.openrangelabs.services.signing.dao.GroupInvite;
import com.openrangelabs.services.signing.exceptions.SNException;

import java.util.List;

public interface DocumentGroups {
    DocumentGroup getDocumentGroup(String documentGroupId) throws SNException;

    String createDocumentGroup(List<String> documentIds, String groupName) throws SNException;

    DocumentGroup.DocumentGroupsListResponce getUserDocumentGroups(Integer limit, Integer offset) throws SNException;

    void deleteDocumentGroup(String documentGroupId) throws SNException;

    String createDocumentGroupInvite(String documentGroupId, GroupInvite groupInvite) throws SNException;

    void resendInvites(String documentGroupId, String inviteId, String email) throws SNException;
}
