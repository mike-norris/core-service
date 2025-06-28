package com.openrangelabs.services.eula;


import com.openrangelabs.services.eula.entity.Eula;
import com.openrangelabs.services.eula.model.EulaStatusUpdateRequest;
import com.openrangelabs.services.eula.model.EulaStatusUpdateResponse;
import com.openrangelabs.services.eula.model.EulaUserDetails;
import com.openrangelabs.services.eula.model.EulaVersionResponse;
import com.openrangelabs.services.log.LogResponseBodyService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.BONITA_API_TOKEN_NAME;
import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.SESSION_ID_NAME;

@RestController
@RequestMapping("/eula")

public class EulaController {
    EulaService eulaService;
    LogResponseBodyService logService;

    @Autowired
    public EulaController(EulaService eulaService, LogResponseBodyService logService) {
        this.eulaService = eulaService;
        this.logService = logService;
    }

    
    @GetMapping(value = "/")
    public List<Eula> getEulas(@CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                               @CookieValue(SESSION_ID_NAME) String sessionId,
                               HttpServletRequest request) {
        return eulaService.getEulas();
    }

    @GetMapping(value = "/{user_id}")
    public EulaUserDetails getEulaUsersDetails(@CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                               @CookieValue(SESSION_ID_NAME) String sessionId,
                                               @PathVariable Integer user_id,
                                               HttpServletRequest request) {
        return  eulaService.getEulaUserDetails(user_id);
    }

    
    @PostMapping(value = "/status")
    public EulaStatusUpdateResponse getEulaUsersDetails(@CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                        @CookieValue(SESSION_ID_NAME) String sessionId,
                                                        @RequestBody EulaStatusUpdateRequest eulaStatusUpdateRequest,
                                                        HttpServletRequest request) {
        return eulaService.statusUpdateRequest(eulaStatusUpdateRequest);
    }
    
    @GetMapping(value = "/version")
    public EulaVersionResponse getEulaUsersDetails(@CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                   @CookieValue(SESSION_ID_NAME) String sessionId,
                                                   HttpServletRequest request) {
        return eulaService.getLatestEULAVersion();
    }

}
