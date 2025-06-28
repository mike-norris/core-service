package com.openrangelabs.services.billing.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class IntacctCustomer implements Customer {
    @JsonProperty("id")
    Long id;

    @JsonProperty("customerId")
    @JsonAlias("CUSTOMERID")
    String customerId;

    @JsonProperty("customerIdFormatted")
    Long customerIdFormatted;

    @JsonProperty("companyName")
    @JsonAlias("NAME")
    String companyName;

    @JsonProperty("parentId")
    @JsonAlias("PARENTID")
    @Nullable
    String parentId;

    @JsonProperty("parentName")
    @JsonAlias("PARENTNAME")
    @Nullable
    String parentName;

    @JsonProperty("recordno")
    @JsonAlias("RECORDNO")
    String recordNo;

    @JsonProperty("entity")
    @JsonAlias("ENTITY")
    String entity;

    @JsonProperty("contactName")
    @JsonAlias("DISPLAYCONTACT.CONTACTNAME")
    String contactName;

    @JsonProperty("email")
    @JsonAlias("DISPLAYCONTACT.EMAIL1")
    String email;

    @JsonProperty("phone")
    @JsonAlias("DISPLAYCONTACT.PHONE1")
    String phone;

    @JsonProperty("adress1")
    @JsonAlias("DISPLAYCONTACT.MAILADDRESS.ADDRESS1")
    String address1;

    @JsonProperty("address2")
    @JsonAlias("DISPLAYCONTACT.MAILADDRESS.ADDRESS2")
    String address2;

    @JsonProperty("city")
    @JsonAlias("DISPLAYCONTACT.MAILADDRESS.CITY")
    String city;

    @JsonProperty("state")
    @JsonAlias("DISPLAYCONTACT.MAILADDRESS.STATE")
    String state;

    @JsonProperty("zip")
    @JsonAlias("DISPLAYCONTACT.MAILADDRESS.ZIP")
    String zip;

    @JsonProperty("status")
    @JsonAlias("STATUS")
    String status;

    @JsonProperty("onetime")
    @JsonAlias("ONETIME")
    String onetime;

    @JsonProperty("onhold")
    @JsonAlias("ONHOLD")
    String onhold;

    @JsonProperty("fuesebillID")
    @JsonAlias("STAX_BILL")
    String fuesebillID;

    @Nullable
    private Map<String, Object> optional = new HashMap<>();

    public void setCustomerId(String customerId) {
        this.recordNo = recordNo;
        this.customerId = customerId;
        try {
            this.customerIdFormatted = Long.valueOf(customerId.replace("C", "1").replace("A", "9"));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @JsonAnySetter
    public void addOptional(String name, Object value) {
        optional.put(name, value);
    }
    @JsonAnyGetter
    public Object getOptional(String name) {
        return optional.get(name);
    }
}