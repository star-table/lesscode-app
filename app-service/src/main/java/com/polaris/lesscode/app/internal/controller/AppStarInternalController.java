package com.polaris.lesscode.app.internal.controller;

import com.polaris.lesscode.app.entity.AppRelation;
import com.polaris.lesscode.app.internal.api.AppStarApi;
import com.polaris.lesscode.app.service.AppStarService;
import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AppStarInternalController implements AppStarApi {

    @Autowired
    private AppStarService appStarService;

    @ApiOperation("收藏应用")
    @PostMapping
    public Result<Boolean> addStar(@PathVariable("appId") Long appId, @RequestParam("orgId") Long orgId, @RequestParam("userId") Long userId){
        appStarService.addStar(orgId, userId, appId);
        return Result.ok(true);
    }

    @ApiOperation("取消应用收藏")
    @DeleteMapping
    public Result<Boolean> deleteStar(@PathVariable("appId") Long appId, @RequestParam("orgId") Long orgId, @RequestParam("userId") Long userId){
        return Result.ok(appStarService.delStar(orgId, userId, appId));
    }

}
