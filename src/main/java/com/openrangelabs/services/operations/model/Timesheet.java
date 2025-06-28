package com.openrangelabs.services.operations.model;

import lombok.Data;

@Data
public class Timesheet {
    int id;
    String entry_date;
    String author_name;
    double hours;
    String project;
    String project_class;
    String task_class;
}
