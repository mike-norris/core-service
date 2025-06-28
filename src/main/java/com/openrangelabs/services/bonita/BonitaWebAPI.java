package com.openrangelabs.services.bonita;

import com.openrangelabs.services.config.BonitaConfig;
import com.openrangelabs.services.tools.Commons;
import com.openrangelabs.services.user.model.UserCreate;
import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.web.client.BonitaClient;
import org.bonitasoft.web.client.api.*;
import org.bonitasoft.web.client.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class BonitaWebAPI {
    BonitaConfig bonitaConfig;
    BonitaClient bonitaClient;

    @Autowired
    BonitaWebAPI(BonitaConfig bonitaConfig){
        this.bonitaConfig = bonitaConfig;
    }

    public void connect(){
        this.bonitaConfig.init();
        this.bonitaClient = bonitaConfig.client;
    }

    public Role getRole(String roleName){
        this.connect();
        log.info("Getting role with name - " + roleName);
        List<String> filters = Arrays.asList("role=" + roleName.toLowerCase());
        List<Role> roles;
        try {
            roles = bonitaClient.get(RoleApi.class).searchRoles(0, 1, filters, null, null);
            log.info("Got role " + roles.get(0));
            return roles.get(0);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public Group getGroup(String groupName){
        this.connect();
        log.info("Getting group with name - " + groupName);
        List<String> filters = Arrays.asList("name=" + groupName);
        List<Group> groups =  bonitaClient.get(GroupApi.class).searchGroups(0,1,filters,null);

        log.info("Got group " + groups.get(0));
        return groups.get(0);

    }

    public User getUser(String userName){
        this.connect();
        log.info("Getting user with name - " + userName);
        List<String> filters = Arrays.asList("userName=" + userName);
        UserApi.SearchUsersQueryParams searchUsersQueryParams = new UserApi.SearchUsersQueryParams();
        searchUsersQueryParams.f(filters);
        List<User> users =  bonitaClient.get(UserApi.class).searchUsers(searchUsersQueryParams);

        log.info("Got user " + users.get(0));
        return users.get(0);

    }

    public User createUser(UserCreate userCreate) {
        this.connect();
        log.info("Creating new user in bonita.");

        UserCreateRequest userCreateRequest = new org.bonitasoft.web.client.model.UserCreateRequest();
        userCreateRequest.setFirstname(Commons.capitalizeFirst(userCreate.getFirstName()));
        userCreateRequest.setLastname(Commons.capitalizeFirst(userCreate.getLastName()));
        userCreateRequest.setUserName(Commons.usernameGenerator(userCreate.getFirstName(), userCreate.getLastName()));
        userCreateRequest.setPassword("secret");
        userCreateRequest.setPasswordConfirm("secret");
        userCreateRequest.setEnabled("true");

        return bonitaClient.get(UserApi.class).createUser(userCreateRequest);

    }

    public Membership addMembership(String roleId, String groupId, String userId) {
        this.connect();
        log.info("Adding membership to bonita user ID " + userId);
        MembershipCreateRequest membershipCreateRequest = new MembershipCreateRequest();
        membershipCreateRequest.setUserId(userId);
        membershipCreateRequest.setGroupId(groupId);
        membershipCreateRequest.setRoleId(roleId);
        return  bonitaClient.get(MembershipApi.class).createMembership(membershipCreateRequest);
    }
    public void addProfile(String userId) {
        this.connect();
        log.info("Adding profile to bonita user ID " + userId);
        List<String> filters = Arrays.asList("name=" + "User");
        List<Profile> profiles =  bonitaClient.get(ProfileApi.class).searchProfiles(0,1 ,filters , null ,null);
        log.info("Got profile " +profiles.get(0));
        ProfileMemberCreateRequest profileMemberCreateRequest = new ProfileMemberCreateRequest();
        profileMemberCreateRequest.setUserId(userId);
        profileMemberCreateRequest.setProfileId(profiles.get(0).getId());

        bonitaClient.get(ProfileMemberApi.class).createProfileMember(profileMemberCreateRequest);

    }

    public void createOrganization(String name, long organizationId) {
        this.connect();
        Group customersGroup = getGroup("customers");
        if (customersGroup == null) {
            throw new RuntimeException("Parent group 'Customers' not found.");
        }
        log.info("Creating bonita group for organization. " +organizationId);
        GroupCreateRequest groupCreateRequest = new GroupCreateRequest();
        groupCreateRequest.setName(String.valueOf(organizationId));
        groupCreateRequest.setDisplayName(name);
        groupCreateRequest.setParentGroupId(customersGroup.getId());
        bonitaClient.get(GroupApi.class).createGroup(groupCreateRequest);
    }
}
