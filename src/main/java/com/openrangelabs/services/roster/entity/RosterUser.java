package com.openrangelabs.services.roster.entity;


import com.openrangelabs.services.documents.entity.Document;
import com.openrangelabs.services.organization.model.OrganizationUser;
import com.openrangelabs.services.signing.modelNew.DocumentInvite;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class RosterUser {

    private long id;
    private long userId;
    private int personnelId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private Boolean isActive;
    private long organizationId;
    private String companyName;
    private Boolean badgeRequired;
    private String positionTitle;
    private int createdBy;
    private OffsetDateTime createdDt;
    private int authorizationBy;
    private OffsetDateTime authorizationDt;

    private List<UserAccess> userAccess;
    private List<RosterUserDatacenter> datacenterList;
    private List<Document> documents;
    private List<DocumentInvite> documentInvites;
    OrganizationUser owner;

}
