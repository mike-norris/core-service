package com.openrangelabs.services.release.model;

import lombok.Data;
import java.util.Date;

@Data
public class ReleaseRecordRequest {
    Date publishDate;
    Date releaseDate;
    String version;
    String pdfLocation;
}
