package com.openrangelabs.services.operations.model;

import lombok.Data;


@Data
public class TagRequest {
    String tag;
    int id;
    String action;
}
