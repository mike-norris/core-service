package com.openrangelabs.services.release;

import com.openrangelabs.services.message.ErrorMessageService;
import com.openrangelabs.services.release.dao.ReleaseBloxopsDAO;
import com.openrangelabs.services.release.model.*;
import com.openrangelabs.services.release.model.*;
import com.openrangelabs.services.release.storage.S3ReleaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ReleaseService {
    ReleaseBloxopsDAO releaseBloxopsDAO;
    S3ReleaseService s3ReleaseService;
    ErrorMessageService errorMessageService;

    String ERROR = "Could not find Release Records";

    @Autowired
    public ReleaseService(ReleaseBloxopsDAO releaseBloxopsDAO, S3ReleaseService s3ReleaseService,
                          ErrorMessageService errorMessageService) {
        this.s3ReleaseService = s3ReleaseService;
        this.errorMessageService = errorMessageService;
        this.releaseBloxopsDAO = releaseBloxopsDAO;
    }

    public ReleaseResponse getReleaseRecords(Integer user_id) {
        List<Release> releases;
        try {
            releases = releaseBloxopsDAO.findAll(user_id);
            if(releases.isEmpty()) {
                log.error(ERROR);
                return new ReleaseResponse(ERROR);
            }
        } catch (Exception e){
            log.error(ERROR);
            log.error(e.getMessage());
            return new ReleaseResponse(ERROR);
       }
       return new ReleaseResponse(releases);
    }

    public ReleaseResponse  getLatestReleaseRecord() {
        List<Release> releases;
        try {
            releases = releaseBloxopsDAO.findLatest();
            if(releases.isEmpty()) {
                log.error(ERROR);
                return new ReleaseResponse(ERROR);
            }
        } catch (Exception e){
            log.error(ERROR);
            log.error(e.getMessage());
            return new ReleaseResponse(ERROR);
        }
        return new ReleaseResponse(releases);
    }

    public ReleaseViewedRecordResponse addViewedRecord(ReleaseViewedRecordRequest addRecordRequest) {
        try {
            releaseBloxopsDAO.addViewedReleaseRecord(addRecordRequest);
            return new ReleaseViewedRecordResponse(true);
        } catch (Exception e) {
            return new ReleaseViewedRecordResponse("Could not add viewed release record.");
        }

    }

    public ReleaseRecordResponse addNewReleaseRecord(ReleaseRecordRequest recordRequest) {
        try {
            releaseBloxopsDAO.addReleaseRecord(recordRequest);
            return new ReleaseRecordResponse(true);
        } catch (Exception e) {
            return new ReleaseRecordResponse(ERROR);
        }

    }

    public ReleaseRecordResponse updateReleaseRecord(Integer release_id, ReleaseRecordRequest updateReleaseRecordRequest) {
        try {
            releaseBloxopsDAO.updateReleaseRecord(release_id ,updateReleaseRecordRequest);
            return new ReleaseRecordResponse(true);
        } catch (Exception e) {
            return new ReleaseRecordResponse("Could not update release record.");
        }

    }

    public ReleaseRecordResponse deleteReleaseRecord(Integer release_id) {
        try {
            releaseBloxopsDAO.deleteReleaseRecord(release_id );
            return new ReleaseRecordResponse(true);
        } catch (Exception e) {
            return new ReleaseRecordResponse("Could not delete release record.");
        }

    }

}
