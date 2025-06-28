package com.openrangelabs.services.release;

import com.openrangelabs.services.log.LogResponseBodyService;
import com.openrangelabs.services.release.model.*;


import com.openrangelabs.services.release.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.BONITA_API_TOKEN_NAME;
import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.SESSION_ID_NAME;

@RestController
@RequestMapping("/release")

public class ReleaseController {

    ReleaseService releaseService;
    LogResponseBodyService logService;

    @Autowired
    public ReleaseController(ReleaseService releaseService, LogResponseBodyService logService) {
        this.releaseService = releaseService;
        this.logService = logService;
    }

    
    @GetMapping(value = "/{user_id}")
    public ReleaseResponse getReleases(@CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                       @CookieValue(SESSION_ID_NAME) String sessionId,
                                       @PathVariable Integer user_id,
                                       HttpServletRequest request) {

        ReleaseResponse response = releaseService.getReleaseRecords(user_id);
        return response;
    }

    @GetMapping(value = "/latest")
    public ReleaseResponse getLatestRelease(@CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                       @CookieValue(SESSION_ID_NAME) String sessionId,
                                       HttpServletRequest request) {
        ReleaseResponse response = releaseService.getLatestReleaseRecord();
        return response;
    }


    @PostMapping(value = "/viewed")
    public ReleaseViewedRecordResponse addViewedRecord(@RequestBody ReleaseViewedRecordRequest addRecordRequest,
                                                       @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                       @CookieValue(SESSION_ID_NAME) String sessionId,
                                                       HttpServletRequest request) {
        ReleaseViewedRecordResponse response = releaseService.addViewedRecord(addRecordRequest);

        return response;
    }

    @PostMapping(value = "/")
    public ReleaseRecordResponse addNewRecord(@RequestBody ReleaseRecordRequest recordRequest,
                                              @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                              @CookieValue(SESSION_ID_NAME) String sessionId,
                                              HttpServletRequest request) {
        ReleaseRecordResponse response = releaseService.addNewReleaseRecord(recordRequest);

        return response;
    }
    
    @PutMapping(value = "/{release_id}")
    public ReleaseRecordResponse addNewRecord(@RequestBody ReleaseRecordRequest recordRequest,
                                              @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                              @CookieValue(SESSION_ID_NAME) String sessionId,
                                              @PathVariable Integer release_id,
                                              HttpServletRequest request) {
        ReleaseRecordResponse response = releaseService.updateReleaseRecord(release_id , recordRequest);

        return response;
    }
    
    @DeleteMapping(value = "/{release_id}")
    public ReleaseRecordResponse deleteRecord(@CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                              @CookieValue(SESSION_ID_NAME) String sessionId,
                                              @PathVariable Integer release_id,
                                              HttpServletRequest request) {
        ReleaseRecordResponse response = releaseService.deleteReleaseRecord(release_id);

        return response;
    }


}