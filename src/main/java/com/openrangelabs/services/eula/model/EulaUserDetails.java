package com.openrangelabs.services.eula.model;

import lombok.Data;

@Data
public class EulaUserDetails {
    String version;
    String status;
    int eula_id;
    String created_date_time;
    String updated_date_time;

    public EulaUserDetails(int eula_id, String created_date_time, String version, String status, String updated_date_time) {
        this.eula_id = eula_id;
        this.created_date_time = created_date_time;
        this.version =version;
        this.status = status;
        this.updated_date_time = updated_date_time;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getEula_id() {
        return eula_id;
    }

    public void setEula_id(int eula_id) {
        this.eula_id = eula_id;
    }
    public String getCreated_date_time() {
        return created_date_time;
    }

    public void setCreated_date_time(String created_date_time) {
        this.created_date_time = created_date_time;
    }

    public String getUpdated_date_time() {
        return updated_date_time;
    }

    public void setUpdated_date_time(String updated_date_time) {
        this.updated_date_time = updated_date_time;
    }
}
