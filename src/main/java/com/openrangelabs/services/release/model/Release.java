package com.openrangelabs.services.release.model;

import lombok.Data;

@Data
public class Release {
    int release_id;
    String publish_date;
    String version;
    String pdf_location;
    String viewed;
    int year;
    int month;
    int day;


    public Release(int release_id, String publish_date, String version,String viewed, String pdf_location,int year, int month, int day) {
        this.release_id = release_id;
        this.publish_date =publish_date;
        this.version =version;
        this.pdf_location = pdf_location;
        this.viewed =viewed;
        this.year =year;
        this.month =month;
        this.day=day;

    }

    public int getRelease_id() {
        return release_id;
    }

    public void setRelease_id(int release_id) {
        this.release_id = release_id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getViewed() {
        return viewed;
    }

    public void setViewed(String viewed) {
        this.viewed = viewed;
    }

    public String getPdf_location() {
        return pdf_location;
    }

    public void setPdf_location(String pdf_location) {
        this.pdf_location = pdf_location;
    }
}