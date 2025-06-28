package com.openrangelabs.services.authenticate.interceptor;

import com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService;
import com.openrangelabs.services.authenticate.model.SessionInfo;
import com.openrangelabs.services.authenticate.permission.bloxops.dao.PermissionDAO;
import com.openrangelabs.services.authenticate.permission.model.Organization;
import com.openrangelabs.services.authenticate.tools.Security;
import com.openrangelabs.services.message.ErrorMessageService;
import com.openrangelabs.services.user.bonita.BonitaUserAPIService;
import com.openrangelabs.services.user.model.UserIdentificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.BONITA_API_TOKEN_NAME;
import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.SESSION_ID_NAME;

@Slf4j
@Component
public class AuthenticateInterceptor implements HandlerInterceptor {

    @Value("${middlewareAdminKey}")
    String middlewareAdminKey;

    BonitaAuthenticateAPIService bonitaAuthenticateAPIService;
    BonitaUserAPIService bonitaUserAPIService;
    ErrorMessageService errorMessageService;
    PermissionDAO permissionDAO;
    Security security;

    @Autowired
    public AuthenticateInterceptor(BonitaAuthenticateAPIService bonitaAuthenticateAPIService,  BonitaUserAPIService bonitaUserAPIService,
                                   ErrorMessageService errorMessageService, PermissionDAO permissionDAO, Security security) {
        this.bonitaAuthenticateAPIService = bonitaAuthenticateAPIService;
        this.bonitaUserAPIService = bonitaUserAPIService;
        this.errorMessageService = errorMessageService;
        this.permissionDAO = permissionDAO;
        this.security = security;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws IOException {
        String path = request.getServletPath();
        String query = request.getQueryString();
        String middlewareAdminKeyMD5 = security.getHash(middlewareAdminKey);
        try {
            log.info("KEY=" + middlewareAdminKeyMD5);
            log.info("PATH=" + path);
            log.info("QUERY=" + query);

            if (query.contains(middlewareAdminKeyMD5)) {
                return true;
            }
        } catch (Exception e) {
            log.info("Not an admin/operations call. Required authentication engaged.");
        }

        UserIdentificationResponse user;
        try {
            Cookie[] cookies = request.getCookies();
            String tokenCookie = "";
            String sessionCookie = "";

            try {
                if (null != cookies) {
                    for (Cookie cookie : cookies) {
                        if (BONITA_API_TOKEN_NAME.equals(cookie.getName())) {
                            tokenCookie = cookie.getValue();
                        } else if (SESSION_ID_NAME.equals(cookie.getName())) {
                            sessionCookie = cookie.getValue();
                        }
                    }
                } else {
                    request.setAttribute("user", null);
                    log.error("Could not authorize request is missing manditory headers ");
                    response.getWriter().write("{ \"error \": \""+errorMessageService.getAuthenicateErrorMessage(1)+"\" }");
                    response.getWriter().flush();
                    response.getWriter().close();
                    return false;
                }
            } catch (Exception e) {
                request.setAttribute("user", null);
                log.error("Could not authorize request is missing manditory headers "+e.getMessage());
                response.getWriter().write("{ \"error\": \""+errorMessageService.getAuthenicateErrorMessage(1)+"\" }");
                response.getWriter().flush();
                response.getWriter().close();
                return false;
            }
            response.addHeader("Cache-Control", "no-store");
            log.info("Attempting to authenticate with x-bonita-api-token: " + tokenCookie);
            user = bonitaAuthenticateAPIService.getUserIdentification(new SessionInfo(tokenCookie, sessionCookie));
            request.setAttribute("user", user);

            Long userId = 0L;
            try {
                userId = Long.parseLong(user.getUserId());
            } catch (Exception e) {
                userId = 0L;
            }

            List<Organization> organizations = permissionDAO.getOrganizationsForUser(userId);

            try {
                if (organizations != null) {
                    return true;
                }
            } catch (Exception e) {
                log.warn("Could not get organizationId from database for userid:"+userId);
            }

            return false;
        } catch (Exception e) {
            request.setAttribute("user", null);
            log.error("Could not authorize user with Bonita: "+e.getMessage());
            response.getWriter().write("{ \" error\": \""+errorMessageService.getAuthenicateErrorMessage(1)+"\" }");
            response.getWriter().flush();
            response.getWriter().close();
            return false;
        }

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
        response.addHeader("Cache-Control", "no-store");
    }

}