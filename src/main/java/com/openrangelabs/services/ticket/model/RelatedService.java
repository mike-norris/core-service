package com.openrangelabs.services.ticket.model;

public class RelatedService {
    String service;
    String source;
    String sourceId;
    String sourceParams;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceParams() {
        return sourceParams;
    }

    public void setSourceParams(String sourceParams) {
        this.sourceParams = sourceParams;
    }
}
