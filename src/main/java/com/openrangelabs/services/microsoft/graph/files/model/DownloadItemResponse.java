package com.openrangelabs.services.microsoft.graph.files.model;
import lombok.Data;

import java.io.File;

@Data
public class DownloadItemResponse {
    File file;
    String fileName;
    String error;

    public DownloadItemResponse(File file,String fileName, String error) {
        this.file = file;
        this.fileName =fileName;
        this.error = error;
    }

}
