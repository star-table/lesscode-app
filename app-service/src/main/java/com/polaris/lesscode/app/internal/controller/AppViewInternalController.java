package com.polaris.lesscode.app.internal.controller;

import com.polaris.lesscode.app.internal.api.AppViewApi;
import com.polaris.lesscode.app.internal.req.CreateViewReq;
import com.polaris.lesscode.app.internal.resp.AppViewResp;
import com.polaris.lesscode.app.internal.service.AppViewInternalService;
import com.polaris.lesscode.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * App视图  （内部调用）
 *
 * @author roamer
 * @version v1.0
 * @date 2021/1/26 10:52
 */
@RestController
public class AppViewInternalController implements AppViewApi {

    @Autowired
    private AppViewInternalService appViewInternalService;

    @Override
    public Result<List<AppViewResp>> getAppViewIdList(Long orgId, Long appId, Boolean includePublic, Long owner) {
        return Result.ok(appViewInternalService.getAppViewList(orgId, appId, includePublic, owner));
    }

    @Override
    public Result<List<AppViewResp>> createAppViews(List<CreateViewReq> reqs) {
        return Result.ok(appViewInternalService.createAppViews(reqs));
    }
}
