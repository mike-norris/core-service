package com.openrangelabs.services.signing.model;

import lombok.Data;

@Data
public class DownloadLinkResponse {
    String link;
    String error;

    public DownloadLinkResponse(String link ,String error) {
        this.link = link;
        this.error = error;
    }

    public DownloadLinkResponse(String error) {
        this.error = error;
    }

    public DownloadLinkResponse() {
    }
}
