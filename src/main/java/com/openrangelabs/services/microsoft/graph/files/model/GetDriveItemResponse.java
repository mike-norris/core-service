package com.openrangelabs.services.microsoft.graph.files.model;

import com.openrangelabs.services.microsoft.graph.files.entity.OneDriveItem;
import lombok.Data;

@Data
public class GetDriveItemResponse {

    OneDriveItem driveItem;
    String error;

    public GetDriveItemResponse(OneDriveItem driveItem, String error) {
        this.driveItem =driveItem;
        this.error = error;
    }
}
