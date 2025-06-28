package com.openrangelabs.services.organization.model;

public class Organization {
    long organizationId;
    long fusebillId;
    long parentId;
    String name;
    String icon;
    String orgCode;
    String status;
    String intacctID;
    String billingPlatform;
    String phoneNumber;
    String emailAddress;
    String address1;
    String address2;
    String city;
    String state;
    String zipcode;
    int salesforceId;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public int getSalesforceId() {
        return salesforceId;
    }

    public void setSalesforceId(int salesforceId) {
        this.salesforceId = salesforceId;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public String getIntacctID() {
        return intacctID;
    }

    public void setIntacctID(String intacctID) {
        this.intacctID = intacctID;
    }

    public String getBillingPlatform() {
        return billingPlatform;
    }

    public void setBillingPlatform(String billingPlatform) {
        this.billingPlatform = billingPlatform;
    }

    public long getFusebillId() {
        return fusebillId;
    }

    public void setFusebillId(long fusebillId) {
        this.fusebillId = fusebillId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
