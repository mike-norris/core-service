package com.openrangelabs.services.ticket.metadefender.model;
import lombok.Data;

@Data
public class SanitizedFileResult {
    String file_path;
    String data_id;
    String result;
}
