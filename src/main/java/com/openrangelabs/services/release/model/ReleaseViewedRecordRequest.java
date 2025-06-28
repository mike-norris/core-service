package com.openrangelabs.services.release.model;

import lombok.Data;

@Data
public class ReleaseViewedRecordRequest {
    int userId;
    int releaseId;

    public int getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(int releaseId) {
        this.releaseId = releaseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}





