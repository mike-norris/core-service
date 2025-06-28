package com.openrangelabs.services.signing.modelNew;

import lombok.Data;

import java.util.List;

@Data
public class GetFolderResponse {
    String id;
    String created;
    String name;
    String user_id;
    String parent_id;
    Boolean system_folder;
    Boolean shared;
    List<Folder> folders;
    String total_documents;
    List<DocumentShort> documents;

}
