package com.openrangelabs.services.operations.model;

import com.openrangelabs.services.log.model.LogRecord;
import lombok.Data;

import java.util.List;

@Data
public class GetLogsResponse {
    List<LogRecord> logs;
    String error;
}
