package com.openrangelabs.services.microsoft.graph.files.model;

import com.openrangelabs.services.microsoft.graph.files.entity.OneDriveItem;
import lombok.Data;

import java.util.List;

@Data
public class GetDriveItemsResponse {
    List<OneDriveItem> driveItemList;
    String error;

    public GetDriveItemsResponse(List<OneDriveItem> driveItemList, String error) {
        this.driveItemList = driveItemList;
        this.error = error;
    }
}
