package com.openrangelabs.services.authenticate.model;

import lombok.Data;

import java.util.List;

@Data
public class GetDropDownOptions {
 List<String> storagePackageOptions;
 List<String> storageTypeOptions;
 String error;

    public GetDropDownOptions(List<String> storagePackageOptions, List<String> storageTypeOptions) {
        this.storagePackageOptions = storagePackageOptions;
        this.storageTypeOptions = storageTypeOptions;
    }

    public GetDropDownOptions(String error) {
        this.error =error;
    }
}
