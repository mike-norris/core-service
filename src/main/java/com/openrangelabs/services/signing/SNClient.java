package com.openrangelabs.services.signing;

import com.openrangelabs.services.signing.dao.*;
import com.openrangelabs.services.signing.exceptions.*;
import com.openrangelabs.services.signing.facades.*;
import com.openrangelabs.services.signing.services.*;
import com.openrangelabs.services.signing.dao.AuthError;
import com.openrangelabs.services.signing.dao.Errors;
import com.openrangelabs.services.signing.dao.User;
import com.openrangelabs.services.signing.exceptions.SNApiException;
import com.openrangelabs.services.signing.exceptions.SNException;
import com.openrangelabs.services.signing.facades.DocumentGroups;
import com.openrangelabs.services.signing.facades.Documents;
import com.openrangelabs.services.signing.facades.ServiceProvider;
import com.openrangelabs.services.signing.facades.Templates;
import com.openrangelabs.services.signing.services.DocumentGroupsService;
import com.openrangelabs.services.signing.services.DocumentsService;
import com.openrangelabs.services.signing.services.TemplatesService;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

public class SNClient implements ServiceProvider {

    public static final String CLIENT_NAME = "SignNow Java API Client";
    public static final String CLIENT_INFO = System.getProperty("os.name") + "; "
            + System.getProperty("os.version") + "; "
            + System.getProperty("os.arch");
    public static final String USER_AGENT = CLIENT_NAME + "/"
            + SNClient.class.getPackage().getImplementationVersion()
            + " (" + CLIENT_INFO + ") "
            + System.getProperty("java.vendor") + "/" + System.getProperty("java.version");

    private User user;
    private WebTarget apiWebTarget;
    private DocumentsService documentsService = new DocumentsService(this);
    private TemplatesService templatesService = new TemplatesService(this);
    private DocumentGroups documentGroupsService = new DocumentGroupsService(this);

    protected SNClient(WebTarget apiWebTarget, User user) {
        this.apiWebTarget = apiWebTarget;
        this.user = user;
    }

    public static void checkAPIException(Response response) throws SNException {
        if (response.getStatus() == 401 || response.getStatus() == 403) {
            throw new SNApiException(response.getStatus() + ": " + response.readEntity(AuthError.class).error);
        } else if (response.getStatus() >= 400) {
            throw new SNApiException(response.readEntity(Errors.class).errors);
        }
    }

    @Override
    public Documents documentsService() {
        return documentsService;
    }

    @Override
    public Templates templatesService() {
        return templatesService;
    }

    @Override
    public DocumentGroups documentGroupsService() {
        return documentGroupsService;
    }

    public WebTarget getApiWebTarget() {
        return apiWebTarget;
    }

    public User getUser() {
        return user;
    }

    public <T> T get(String path, Map<String, String> parameters, Class<T> returnType) throws SNException {
        Response response = buildRequest(path, parameters).get();
        checkAPIException(response);
        return response.readEntity(returnType);
    }

    public <T> T get(String path, Map<String, String> parameters, GenericType<T> returnType) throws SNException {
        Response response = buildRequest(path, parameters).get();
        checkAPIException(response);
        return response.readEntity(returnType);
    }

    public <E, T> T post(String path, Map<String, String> parameters, E inputData, Class<T> returnType) throws SNException {
        Response response = buildRequest(path, parameters).post(Entity.entity(inputData, SNClientBuilder.defaultVariant));
        checkAPIException(response);
        return response.readEntity(returnType);
    }

    public <E, T> T post(String path, Map<String, String> parameters, E inputData, GenericType<T> returnType) throws SNException {
        Response response = buildRequest(path, parameters).post(Entity.entity(inputData, SNClientBuilder.defaultVariant));
        checkAPIException(response);
        return response.readEntity(returnType);
    }

    public <E, T> T put(String path, Map<String, String> parameters, E inputData, Class<T> returnType) throws SNException {
        Response response = buildRequest(path, parameters).put(Entity.entity(inputData, SNClientBuilder.defaultVariant));
        checkAPIException(response);
        return response.readEntity(returnType);
    }

    public <E, T> T delete(String path, Map<String, String> parameters, Class<T> returnType) throws SNException {
        Response response = buildRequest(path, parameters).delete();
        checkAPIException(response);
        return response.readEntity(returnType);
    }

    private Invocation.Builder buildRequest(String path, Map<String, String> parameters) {
        WebTarget target = apiWebTarget.path(path);
        if (parameters != null) {
            for (String key : parameters.keySet()){
                WebTarget targetUpd = target.resolveTemplate(key, parameters.get(key));
                if (!targetUpd.toString().equals(target.toString())) {
                    target = targetUpd;
                } else {
                    target = target.queryParam(key, parameters.get(key));
                }
            }
        }
        return target.request(MediaType.APPLICATION_JSON_TYPE)
                .header(Constants.AUTHORIZATION, Constants.BEARER + user.getToken())
                .header("User-Agent", USER_AGENT);
    }

    public User.UserAuthResponce checkAuth() throws SNException {
        return get("oauth2/token", null, User.UserAuthResponce.class);
    }

    public void refreshToken() throws SNException {
        SNClientBuilder.get().refreshToken(user);
    }
}
