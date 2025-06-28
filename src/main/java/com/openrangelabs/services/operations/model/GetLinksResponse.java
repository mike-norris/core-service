package com.openrangelabs.services.operations.model;

import lombok.Data;

import java.util.List;

@Data
public class GetLinksResponse {
    List<Link> links;
    String error;

    public GetLinksResponse(List<Link> links, String error) {
        this.links = links;
        this.error = error;
    }

    public GetLinksResponse() {

    }
}
