package com.openrangelabs.services.authenticate.permission;

import com.openrangelabs.services.authenticate.permission.model.OpenFga.FGAPermissionRequest;
import com.openrangelabs.services.authenticate.permission.model.OpenFga.FGAPermissionResponse;
import com.openrangelabs.services.authenticate.permission.model.PermissionListResponse;
import com.openrangelabs.services.authenticate.permission.model.PermissionsResponse;
import com.openrangelabs.services.log.LogResponseBodyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.management.ManagementFactory;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    PermissionService permissionService;
    LogResponseBodyService logService;
    OpenFGAService openFgaService;

    @Autowired()
    public void permissionController(PermissionService permissionService, LogResponseBodyService logService , OpenFGAService openFgaService) {
        this.permissionService = permissionService;
        this.logService = logService;
        this.openFgaService = openFgaService;
    }
    /* operations - get list of all permissions */
    @GetMapping(path = "/list")
    public PermissionListResponse getPermissionList(HttpServletRequest request) {
        return permissionService.getPermissionsList();
    }
    /* operations get user permissions */
    @GetMapping(path = "/{orgId}/{userId}/{module}")
    public PermissionsResponse getUserPermissions(@PathVariable("userId") Long userId,
                                                  @PathVariable("orgId") Long orgId,
                                                  @PathVariable("module") String module,
                                                  HttpServletRequest request) {

        if(module.equals("all")){
            return (PermissionsResponse) logService.logResponse(
                    permissionService.getAllServicePermissons(userId,orgId),
                    request.getMethod(),
                    request.getPathInfo(),
                    ManagementFactory.getRuntimeMXBean().getName());

        }else{
            return (PermissionsResponse) logService.logResponse(
                    permissionService.getServicePermissons(userId, Long.parseLong(module), orgId),
                    request.getMethod(),
                    request.getPathInfo(),
                    ManagementFactory.getRuntimeMXBean().getName());

        }

    }

    //OpenFga endpoints for pandora 2.0
    @PostMapping(path = "/add")
    public FGAPermissionResponse addPermission(@RequestBody FGAPermissionRequest fgaPermissionRequest,
                                               HttpServletRequest request) {

            return (FGAPermissionResponse) logService.logResponse(
                    openFgaService.addPermission(fgaPermissionRequest),
                    request.getMethod(),
                    request.getPathInfo(),
                    ManagementFactory.getRuntimeMXBean().getName());

    }

    @PostMapping(path = "/remove")
    public FGAPermissionResponse removePermission(@RequestBody FGAPermissionRequest fgaPermissionRequest,
                                               HttpServletRequest request) {

        return (FGAPermissionResponse) logService.logResponse(
                openFgaService.removePermission(fgaPermissionRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());

    }

    @PostMapping(path = "/check")
    public FGAPermissionResponse checkPermission(@RequestBody FGAPermissionRequest fgaPermissionRequest,
                                                  HttpServletRequest request) {

        return (FGAPermissionResponse) logService.logResponse(
                openFgaService.checkPermission(fgaPermissionRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());

    }

    @PostMapping(path = "/list/user")
    public FGAPermissionResponse listUserPermissions(@RequestBody FGAPermissionRequest fgaPermissionRequest,
                                                 HttpServletRequest request) {

        return (FGAPermissionResponse) logService.logResponse(
                openFgaService.listUserPermissions(fgaPermissionRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());

    }

}
