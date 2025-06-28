package com.openrangelabs.services.authenticate.permission;

import com.openrangelabs.services.authenticate.permission.model.OpenFga.FGAPermissionRequest;
import com.openrangelabs.services.authenticate.permission.model.OpenFga.FGAPermissionResponse;
import com.openrangelabs.services.authenticate.permission.model.OpenFga.TupleKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OpenFGAService {

    OpenFgaAPIService openFgaAPIService;

    @Autowired
    public OpenFGAService(OpenFgaAPIService openFgaAPIService) {
        this.openFgaAPIService = openFgaAPIService;
    }


    public FGAPermissionResponse addPermission(FGAPermissionRequest fgaPermissionRequest) {
        FGAPermissionResponse fgaPermissionResponse = new FGAPermissionResponse();
        try{
            boolean success = openFgaAPIService.writeAuthorizationTuple(fgaPermissionRequest);
                    if(success){
                        fgaPermissionResponse.setAllowed(true);
                    }else{
                        fgaPermissionResponse.setAllowed(false);
                    }
        }catch(Exception e){
            fgaPermissionResponse.setError("Error :" + e.getMessage());
        }
        return fgaPermissionResponse;
    }

    public FGAPermissionResponse removePermission(FGAPermissionRequest fgaPermissionRequest) {
        FGAPermissionResponse fgaPermissionResponse = new FGAPermissionResponse();
        try{
            boolean success = openFgaAPIService.deleteAuthorizationTuple(fgaPermissionRequest);
            if(success){
                fgaPermissionResponse.setAllowed(true);
            }else{
                fgaPermissionResponse.setAllowed(false);
            }

        }catch(Exception e){
            fgaPermissionResponse.setError("Error :" + e.getMessage());
        }
        return fgaPermissionResponse;
    }

    public FGAPermissionResponse checkPermission(FGAPermissionRequest fgaPermissionRequest) {
        FGAPermissionResponse fgaPermissionResponse = new FGAPermissionResponse();
        try{
            boolean success = openFgaAPIService.checkPermission(fgaPermissionRequest);
            if(success){
                fgaPermissionResponse.setAllowed(true);
            }else{
                fgaPermissionResponse.setAllowed(false);
            }
        }catch(Exception e){
            fgaPermissionResponse.setError("Error :" + e.getMessage());
        }
        return fgaPermissionResponse;
    }

    public FGAPermissionResponse listUserPermissions(FGAPermissionRequest fgaPermissionRequest) {
        FGAPermissionResponse fgaPermissionResponse = new FGAPermissionResponse();
        try{
            List<TupleKey> permissions = openFgaAPIService.listUserPermissions(fgaPermissionRequest);
            fgaPermissionResponse.setAllowed(true);
            fgaPermissionResponse.setTuples(permissions);
        }catch(Exception e){
            fgaPermissionResponse.setError("Error :" + e.getMessage());
        }
        return fgaPermissionResponse;
    }
}
