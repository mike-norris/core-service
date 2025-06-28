package com.openrangelabs.services.user.bonita;

import com.openrangelabs.middleware.config.WebClientConfig;
import com.openrangelabs.services.authenticate.bonita.model.BonitaPurgeResponse;
import com.openrangelabs.services.user.bonita.model.*;
import com.openrangelabs.services.user.bonita.model.*;
import com.openrangelabs.services.user.entity.User;
import com.openrangelabs.services.user.model.UserCreate;
import com.openrangelabs.services.authenticate.model.SessionInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.BONITA_API_TOKEN_NAME;
import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.SESSION_ID_NAME;

@Slf4j
@Service
public class BonitaUserAPIService {
    private final WebClient webClient;

    @Value("${bonitaURL}")
    public String API_URL;

    protected static final String GROUP_URL = "API/identity/group/";
    protected static final String ROLE_URL = "API/identity/role/";
    protected static final String COOKIE = "Cookie";

    public BonitaUserAPIService() {
        this.webClient = WebClientConfig.build(API_URL);
    }

    public BonitaUserDetails createUser(UserCreateTestRequest userCreateRequest, SessionInfo sessionInfo) {
        try {
            BonitaUserDetails response = webClient.post()
                    .uri(API_URL + "API/identity/user")
                    .header(COOKIE, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                    .header(COOKIE, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                    .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                    .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                    .bodyValue(userCreateRequest)
                    .retrieve()
                    .toEntity(BonitaUserDetails.class)
                    .block()
                    .getBody();
            if (StringUtils.isNotEmpty(response.getId())) {
                return response;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        BonitaUserDetails userDetails = new BonitaUserDetails();
        userDetails.setErrorFields();
        return userDetails;
    }

    public BonitaGroup getGroup(String groupId, SessionInfo sessionInfo) {
        return getGroupResponse(groupId, sessionInfo).getBody();
    }

    public ResponseEntity<BonitaGroup> getGroupResponse(String groupId, SessionInfo sessionInfo) {
        ParameterizedTypeReference<BonitaGroup> responseType = new ParameterizedTypeReference<BonitaGroup>() {};

        return webClient.get()
                .uri(API_URL + GROUP_URL + groupId)
                .header(COOKIE, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                .header(COOKIE, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .retrieve()
                .toEntity(responseType)
                .block();
    }

    public BonitaRole getRole(String roleId, SessionInfo sessionInfo) {
        return getRoleResponse(roleId, sessionInfo).getBody();
    }

    public ResponseEntity<BonitaRole> getRoleResponse(String roleId, SessionInfo sessionInfo) {
        ParameterizedTypeReference<BonitaRole> responseType = new ParameterizedTypeReference<BonitaRole>() {};

        return webClient.get()
                .uri(API_URL + ROLE_URL + roleId)
                .header(COOKIE, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                .header(COOKIE, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .retrieve()
                .toEntity(responseType)
                .block();
    }

    public HttpStatusCode updateUser(UserCreate userCreate, String userId, SessionInfo sessionInfo) {
        ParameterizedTypeReference<BonitaRole> responseType = new ParameterizedTypeReference<BonitaRole>() {};
        try {
            return webClient.put()
                    .uri(API_URL + "API/identity/user/" + userId)
                    .header(COOKIE, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                    .header(COOKIE, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                    .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                    .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                    .bodyValue(userCreate)
                    .retrieve()
                    .toEntity(responseType)
                    .block().getStatusCode();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return HttpStatus.BAD_REQUEST;
    }

    public BonitaUserContactDetails updateProfessionalContactDetails(BonitaUserContactDetails contactDetails, String userId, SessionInfo sessionInfo) {
        try {
            return webClient.put()
                    .uri(API_URL + "API/identity/professionalcontactdata/" + userId)
                    .header(COOKIE, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                    .header(COOKIE, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                    .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                    .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                    .bodyValue(contactDetails)
                    .retrieve()
                    .toEntity(BonitaUserContactDetails.class)
                    .block().getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new BonitaUserContactDetails(userId, contactDetails.getEmail());
    }

    public BonitaUserContactDetails updatePersonalContactDetails(BonitaUserContactDetails contactDetails, String userId, SessionInfo sessionInfo) {
        try {
            return webClient.put()
                    .uri(API_URL + "API/identity/personalcontactdata/" + userId)
                    .header(COOKIE, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                    .header(COOKIE, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                    .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                    .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                    .bodyValue(contactDetails)
                    .retrieve()
                    .toEntity(BonitaUserContactDetails.class)
                    .block().getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new BonitaUserContactDetails(userId, contactDetails.getEmail());
    }

    public BonitaPasswordChangeResponse updatePassword(BonitaPasswordChangeRequest changeRequest, SessionInfo sessionInfo) {
        try {
            log.info("Attempting to Update password with username: "+changeRequest.getUsername());
            log.info("Attempting to Update password with password: "+changeRequest.getNewpassword());
            log.info("Attempting to Update password with token: "+sessionInfo.getSessionToken());
            log.info("Attempting to Update password with session id: "+sessionInfo.getSessionId());
            return webClient.post()
                    .uri(API_URL + "API/extension/ldap/password")
                    .header(COOKIE, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                    .header(COOKIE, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                    .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                    .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                    .bodyValue(changeRequest)
                    .retrieve()
                    .toEntity(BonitaPasswordChangeResponse.class)
                    .block().getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new BonitaPasswordChangeResponse("The password could not be changed at this time. Please try again later.", "");
    }

    public User getUserById(int userId, SessionInfo sessionInfo) {
        try {
            return webClient.get()
                    .uri(API_URL + "API/identity/user/" + userId)
                    .header(COOKIE, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                    .header(COOKIE, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                    .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                    .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                    .retrieve()
                    .toEntity(User.class)
                    .block().getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new User();
    }

    public List<BonitaUserMembership> getUserMembershipsById(int userId, SessionInfo sessionInfo) {
        ParameterizedTypeReference<List<BonitaUserMembership>> responseType = new ParameterizedTypeReference<List<BonitaUserMembership>>() {};
        try {
            return webClient.get()
                    .uri(API_URL + "API/identity/membership?p=0&c=100&f=user_id=" + userId)
                    .header(COOKIE, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                    .header(COOKIE, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                    .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                    .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                    .retrieve()
                    .toEntity(responseType)
                    .block().getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<BonitaUserCustomDetail> getUserCustomInfoById(int userId, SessionInfo sessionInfo) {
        ParameterizedTypeReference<List<BonitaUserCustomDetail>> responseType = new ParameterizedTypeReference<List<BonitaUserCustomDetail>>() {};
        try {
            return webClient.get()
                    .uri(API_URL + "API/customuserinfo/user?c=100&p=0&f=userId=" + userId)
                    .header(COOKIE, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                    .header(COOKIE, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                    .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                    .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                    .retrieve()
                    .toEntity(responseType)
                    .block().getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ArrayList<>();
    }

    public Boolean purgeUser(String userId, String orgId , SessionInfo sessionInfo) {
        ParameterizedTypeReference<BonitaPurgeResponse> responseType = new ParameterizedTypeReference<BonitaPurgeResponse>() {};
        try {
            //TODO
            // WebClient does not allow bodyValue method in a delete() so the Bonita extension might need to be changed
            BonitaPurgeResponse response = webClient.delete()
                    .uri(API_URL + "API/extension/ldap/deleteUser?user_id=" + userId + "&organization_id=" + orgId)
                    .header(COOKIE, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                    .header(COOKIE, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                    .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                    .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                    .retrieve()
                    .toEntity(responseType)
                    .block().getBody();
            if (StringUtils.isNotEmpty(response.getSuccess())) {
                if (response.getSuccess().equals("true")) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

}
