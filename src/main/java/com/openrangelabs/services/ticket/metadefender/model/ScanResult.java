package com.openrangelabs.services.ticket.metadefender.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ScanResult {
    @JsonProperty("file_id")
    String fileId;
    @JsonProperty("data_id")
    String dataId;
    SanitizedFileResult sanitized;
    @JsonProperty("process_info")
    ProcessInfo processInfo;
    @JsonProperty("file_info")
    FileInfo fileInfo;
}
