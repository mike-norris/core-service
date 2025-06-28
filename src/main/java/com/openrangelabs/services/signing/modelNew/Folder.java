package com.openrangelabs.services.signing.modelNew;

import lombok.Data;

@Data
public class Folder {
    String id;
    String user_id;
    String name;
    String created;
    Boolean shared;
    String document_count;
    String template_count;
    String folder_count;
}
