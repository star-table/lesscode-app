package com.polaris.lesscode.app.controller;

import com.polaris.lesscode.app.entity.AppRelation;
import com.polaris.lesscode.app.service.AppStarService;
import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags="应用加星")
@RestController
@RequestMapping("/app/api/v1/apps/{appId}/stars")
public class AppStarController {

    @Autowired
    private AppStarService appStarService;

    @ApiOperation("收藏应用")
    @PostMapping
    public Result<AppRelation> addStar(@PathVariable("appId") Long appId){
        return Result.ok(appStarService.addStar(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId));
    }

    @ApiOperation("取消应用收藏")
    @DeleteMapping
    public Result<Boolean> deleteStar(@PathVariable("appId") Long appId){
        return Result.ok(appStarService.delStar(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId));
    }

}
