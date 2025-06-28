package com.openrangelabs.services.ticket.metadefender.model;
import lombok.Data;

@Data
public class FileInfo {
    private String file_type_extension;
    private String file_type_description;
    private String file_type_category;
    private String sha256;
    private String sha1;
    private String md5;
    private String upload_timestamp;
    private float file_size;
    private String display_name;
}
