package com.openrangelabs.services.signing.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class User {
    private String email;
    private String token;
    private String refreshToken;

    public User() {
    }

    public User(String email, String token, String refreshToken) {
        this.email = email;
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    public static class UserAuthRequest {
        @JsonProperty("grant_type")
        public final String grantType = "password";
        public final String username;
        public final String password;
        public final String scope;

        public UserAuthRequest(String username, String password) {
            this(username, password, "*");
        }

        public UserAuthRequest(String username, String password, String scope) {
            this.username = username;
            this.password = password;
            this.scope = scope;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserAuthResponce {
        @JsonProperty("access_token")
        public String token;
        @JsonProperty("refresh_token")
        public String refreshToken;
        @JsonProperty("expires_in")
        public String expiresIn;
        public String scope;
        public String id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserCreateRequest {
        public String email;
        public String password;
        @JsonProperty("first_name")
        public String firstName;
        @JsonProperty("last_name")
        public String lastName;

        public UserCreateRequest(String email, String password) {
            this(email, password, null, null);
        }

        public UserCreateRequest(String email, String password, String firstName, String lastName) {
            this.email = email;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserCreateResponce {
        public String id;
        public boolean verified;
        public String email;
    }

    public static class UserInfo {
        public String id;
        @JsonProperty("first_name")
        public String firstName;
        @JsonProperty("last_name")
        public String lastName;
        public String active;
        public Integer type;
        public Integer pro;
        public String created;
        public List<String> emails;
        public Integer credits;
        @JsonProperty("has_atticus_access")
        public Boolean hasAtticusAccess;
        @JsonProperty("is_logged_in")
        public Boolean isLoggedIn;
    }

}
