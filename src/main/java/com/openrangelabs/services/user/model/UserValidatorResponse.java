package com.openrangelabs.services.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.openrangelabs.services.authenticate.permission.model.OrganizationService;
import com.openrangelabs.services.organization.model.Organization;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserValidatorResponse implements Serializable {
    @JsonProperty("error")
    boolean error;
    @JsonProperty("error_list")
    List<String> errorList;
    @JsonProperty("organization")
    Organization organization;
    @JsonProperty("in_organization")
    boolean inOrganization;
    @JsonProperty("bon_organization_group")
    boolean bonOrganizationGroup;
    @JsonProperty("organization_services")
    List<OrganizationService> organizationServices;
    @JsonProperty("ml_status")
    boolean mlStatus;
    @JsonProperty("bon_user_status")
    boolean bonUserStatus;
    @JsonProperty("role")
    String role;
    @JsonProperty("has_permissions")
    boolean hasPermissions;
    @JsonProperty("has_permissions_modules")
    List<String> hasPermissionsModules;
    @JsonProperty("ad_account")
    boolean adAccount;
    @JsonProperty("successful_login")
    boolean successfulLogin;

    public void addErrorList(String error) {
        this.errorList.add(error);
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
        if (organization.getStatus().contains("0")) {
            this.addErrorList("The organization is not active in the middleware");
        }
        boolean account = checkService("account");
        boolean support = checkService("support");
        boolean billing = checkService("billing");

        if (!account) {
            this.addErrorList("The organization is missing access to the account module");
        }
        if (!support) {
            this.addErrorList("The organization is missing access to the support module");
        }
        if (!billing) {
            this.addErrorList("The organization is missing access to the billing module");
        }
        if (!this.bonOrganizationGroup) {
            this.addErrorList("The user is not assigned to the organization group in Bonita");
        }
        if (!inOrganization) {
            this.addErrorList("The user is not assigned to the organization group in the middleware");
        }
        if (!mlStatus) {
            this.addErrorList("The user is not active in the middleware");
        }
        if (!bonUserStatus) {
            this.addErrorList("The user is not active in Bonita");
        }
        if (!hasPermissions) {
            this.addErrorList("The user does not have any permissions");
        }
        if (!adAccount) {
            this.addErrorList("The user does not have an AD account");
        }
        if (!successfulLogin) {
            this.addErrorList("The user does not have a successful login");
        }
        if (this.errorList.size() > 0) {
            this.setError(true);
        }
    }

    public boolean checkService(String module){
        Boolean hasService = false;
        for (OrganizationService companyService : this.organizationServices) {
            if (companyService.getName().contains(module)) {
                hasService = true;
            }
        }
        return hasService;
    }
}
