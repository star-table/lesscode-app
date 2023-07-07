package com.polaris.lesscode.app.internal.controller;

import com.polaris.lesscode.app.internal.api.AppActionApi;
import com.polaris.lesscode.app.internal.req.CreateAppActionReq;
import com.polaris.lesscode.app.internal.service.AppActionInternalService;
import com.polaris.lesscode.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppActionInternalController implements AppActionApi {

    @Autowired
    private AppActionInternalService appActionInternalService;

    @Override
    public Result<Boolean> createAction(
            @RequestParam(value = "orgId") Long orgId,
            @RequestParam(value = "operatorId", required = false) Long operatorId,
            @RequestBody CreateAppActionReq req
    ) {
        appActionInternalService.createAction(orgId, operatorId, req);
        return Result.ok(true);
    }
}
