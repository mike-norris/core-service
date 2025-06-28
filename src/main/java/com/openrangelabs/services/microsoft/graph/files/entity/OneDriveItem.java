package com.openrangelabs.services.microsoft.graph.files.entity;

import lombok.Data;

@Data
public class OneDriveItem {
    String createdDT;
    String id;
    String lastModified;
    String name;
    Long size;
    Boolean isFolder;
    String fileType;
    String orgCode;
}
