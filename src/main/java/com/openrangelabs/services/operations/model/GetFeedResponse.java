package com.openrangelabs.services.operations.model;

import lombok.Data;

import java.util.List;

@Data
public class GetFeedResponse {
    String title;
    String description;
    List<Entry> entries;
}
