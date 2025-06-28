package com.openrangelabs.services.release.model;

import lombok.Data;

import java.util.List;

@Data
public class ReleaseResponse {
    List<Release> releases;
    String error;

    public ReleaseResponse(List<Release> releases) {
        this.releases = releases;
    }

    public ReleaseResponse(String error) {
        this.error = error;
    }

    public ReleaseResponse() {
    }
}
