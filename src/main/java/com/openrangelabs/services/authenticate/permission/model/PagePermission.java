package com.openrangelabs.services.authenticate.permission.model;

import lombok.Data;

@Data
public class PagePermission {
    long pageId;
    String pageName;
    String pageDisplayName;
    long componentId;
    String componentName;
    String componentDisplayName;
    String componentDefaultAccess;
    String componentAttributes;
    String componentIdentifier;
    String componentState;
    String componentStatus;
    long componentColumnNumber;
    long componentRowNumber;
    long permissionId;
    String permissionEnabled;
    String permissionAccess;
    String permissionAttribute;
}
