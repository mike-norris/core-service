package com.openrangelabs.services.signing;

import com.openrangelabs.services.signing.dao.AuthError;
import com.openrangelabs.services.signing.dao.Errors;
import com.openrangelabs.services.signing.dao.User;
import com.openrangelabs.services.signing.exceptions.SNAuthException;
import com.openrangelabs.services.signing.exceptions.SNException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.*;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
public class SNClientBuilder {


    @Value("${signNowApiUrl}")
    private String apiUrl;

    @Value("${signNowClientId}")
    private String clientId;

    @Value("${signNowClientSecret}")
    private String clientSecret;
    private String basicAuthHeader;
    private Client basicClient;
    private WebTarget snApiUrl;
    private static volatile SNClientBuilder instance;

    protected static final Variant defaultVariant = new Variant(
            MediaType.APPLICATION_JSON_TYPE,
            Locale.getDefault(),
            StandardCharsets.UTF_8.name()
    );

    public static SNClientBuilder get() throws SNException {
        SNClientBuilder localInstance = instance;
        if (localInstance == null) {
            throw new SNException("SNClientBuilder must be initialized with API connection prerequisites") {};
        }
        return localInstance;
    }

    /**
     * SNClientBuilder singleton initializer
     *
     * @param apiUrl
     * @param clientId
     * @param clientSecret
     * @return
     */
    public static SNClientBuilder get(String apiUrl, String clientId, String clientSecret) {
        SNClientBuilder localInstance = instance;
        if (localInstance == null) {
            synchronized (SNClientBuilder.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SNClientBuilder(apiUrl, clientId, clientSecret);
                }
            }
        }
        return localInstance;
    }

    public SNClientBuilder(String customApiUrl, String customClientId, String customClientSecret) {
        this.apiUrl = customApiUrl;
        this.clientId = customClientId;
        this.clientSecret = customClientSecret;
        this.basicAuthHeader = "Basic "+encodeClientCredentials(clientId, clientSecret);
        try {
            basicClient = ClientBuilder.newClient();
            basicClient.register(MultiPartFeature.class);
            snApiUrl = basicClient.target(apiUrl);
        } catch (Exception e) {
            System.out.println("Error creating client: " + e.getMessage());
        }
    }

    private String encodeClientCredentials(String client, String secret){
        return Base64.getEncoder().encodeToString((client + ":" + secret).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Produces new API client instance for SignNow user
     * @param email
     * @param password
     * @return
     * @throws SNAuthException
     */
    public SNClient getClientForExistingUser(String email, String password) throws SNAuthException {
        User user = null;
        try {
            Form authForm = new Form();
            authForm.param("grant_type", "password")
                    .param("username", email)
                    .param("password", password)
                    .param("scope", "*");
            Response response = getOAuthRequest(authForm);
            User.UserAuthResponce authData = response.readEntity(User.UserAuthResponce.class);
            user = new User(email, authData.token, authData.refreshToken);
        } catch (Exception e) {
            throw new SNAuthException(e.getMessage(), e);
        }
        return new SNClient(
                snApiUrl,
                user
        );
    }

    protected void refreshToken(User user) throws SNException {
        try {
            Form authForm = new Form();
            authForm.param("grant_type", "refresh_token")
                    .param("refresh_token", user.getRefreshToken())
                    .param("scope", "*");
            Response response = getOAuthRequest(authForm);
            User.UserAuthResponce auth = response.readEntity(User.UserAuthResponce.class);
            user.setToken(auth.token);
            user.setRefreshToken(auth.refreshToken);
        } catch (Exception e) {
            throw new SNException(e.getMessage(), e) {};
        }
    }

    private Response getOAuthRequest(Form authForm) throws SNAuthException {
        Response response = snApiUrl.path("/oauth2/token")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(Constants.AUTHORIZATION, basicAuthHeader)
                .post(Entity.entity(authForm, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        if (response.getStatus() >= 500) {
            throw new SNAuthException(response.readEntity(Errors.class).errors.stream()
                    .map(err -> err.code + ": " + err.message)
                    .collect(Collectors.joining("\n")));
        } else if (response.getStatus() >= 400) {
            throw new SNAuthException(response.getStatus() + ": " + response.readEntity(AuthError.class).error);
        }
        return response;
    }

    /**
     * Get new API client instance for already authenticated user. Argument object can be obtained from {@link SNClient#getUser()}
     *
     * @param user
     * @return
     * @throws SNAuthException
     */
    public SNClient getClientForAuthenticatedUser(User user) throws SNAuthException {
        SNClient cli = new SNClient(
                snApiUrl,
                user
        );
        try {
            cli.checkAuth();
        } catch (SNAuthException e) {
            System.out.println("AUTH: " + e.getAuthError() + ", " + e.getMessage());
            if (e.getAuthError() == AuthError.Type.INVALID_TOKEN) {
                try {
                    refreshToken(user);
                } catch (SNException refreshEx) {
                    throw new SNAuthException(refreshEx.getMessage(), refreshEx) {};
                }
            }
        } catch (SNException e) {
            throw new SNAuthException(e.getMessage(), e);
        }
        return cli;
    }

    /**
     * Create new user in SignNow account
     * @param email
     * @param password
     * @return user ID
     * @throws SNAuthException
     */
    public String createUser(String email, String password) throws SNAuthException {
        try {
            Response response = snApiUrl.path("/user")
                    .request(MediaType.APPLICATION_JSON)
                    .header(Constants.AUTHORIZATION, basicAuthHeader)
                    .post(Entity.entity(new User.UserCreateRequest(email, password), defaultVariant));
            if (response.getStatus() >= 400) {
                throw new SNAuthException(response.readEntity(Errors.class).errors.get(0).message);
            } else {
                return response.readEntity(User.UserCreateResponce.class).id;
            }
        } catch (SNAuthException e) {
            throw e;
        } catch (Exception e) {
            throw new SNAuthException(e.getMessage(), e);
        }
    }

}
