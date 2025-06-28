package com.openrangelabs.services.eula.entity;

import lombok.Data;

@Data
public class Eula {
    int eula_id;
    String publishedDate;
    String version;
    String pdfLocation;
    String htmlLocation;
    String createdDateTime;
    String updatedDateTime;

    public Eula(int eula_id, String createdDateTime, String version, String publishedDate, String updatedDateTime, String htmlLocation, String pdfLocation) {
        this.eula_id =eula_id;
        this.createdDateTime =createdDateTime;
        this.version = version;
        this.publishedDate = publishedDate;
        this.updatedDateTime = updatedDateTime;
        this.htmlLocation = htmlLocation;
        this.pdfLocation = pdfLocation;
    }
}
