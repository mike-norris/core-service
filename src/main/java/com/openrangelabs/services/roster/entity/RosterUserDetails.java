package com.openrangelabs.services.roster.entity;

import com.openrangelabs.services.documents.entity.Document;
import com.openrangelabs.services.organization.model.Organization;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class RosterUserDetails {

    private long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private Boolean isActive;
    private long organizationId;
    private String companyName;
    private Boolean badgeRequired;
    private Boolean photoOnFile;
    private String photoLocation;
    private Organization organization;
    private String organizationContactName;
    private String organizationContactMethod;
    private String positionTitle;
    private String ownerEmail;
    private int authorizationBy;
    private OffsetDateTime authorizationDt;

    private List<UserAccess> userAccess;
    private List<RosterUserDatacenter> datacenterList;
    private List<Document> documents;
    private List<RosterUserPhoto> photograph;
}
