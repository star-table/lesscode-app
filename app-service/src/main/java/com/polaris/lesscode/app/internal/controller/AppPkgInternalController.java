package com.polaris.lesscode.app.internal.controller;

import com.polaris.lesscode.app.internal.api.AppPackageApi;
import com.polaris.lesscode.app.internal.resp.AppPackageResp;
import com.polaris.lesscode.app.internal.service.AppPkgInternalService;
import com.polaris.lesscode.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AppPkgInternalController implements AppPackageApi {

    @Autowired
    private AppPkgInternalService appPkgInternalService;


    public Result<List<AppPackageResp>> getAppPkgList(Long orgId) {
        List<AppPackageResp> resps = appPkgInternalService.packageRespList(orgId);
        return Result.ok(resps);
    }

    public Result<AppPackageResp> getAppPkgInfo(Long pkgId) {
        AppPackageResp resp = appPkgInternalService.get(pkgId);
        return Result.ok(resp);
    }
}
