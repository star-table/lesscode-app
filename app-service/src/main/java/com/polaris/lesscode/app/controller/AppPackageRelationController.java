package com.polaris.lesscode.app.controller;

import com.polaris.lesscode.app.req.AppPackageRelationAddReq;
import com.polaris.lesscode.app.req.AppPackageRelationUpdateReq;
import com.polaris.lesscode.app.resp.AppPackageRelationResp;
import com.polaris.lesscode.app.service.AppPackageRelationService;
import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Liu.B.J
 * @Data: 2020/8/31 11:17
 * @Modified:
 */
@Api(tags="应用包关联管理")
@RestController
@RequestMapping("/app/api/v1/app/pkg/relations")
public class AppPackageRelationController {

    @Autowired
    private AppPackageRelationService appPkgRelService;

    @ApiOperation(value="添加应用包关联信息", notes="添加应用包关联信息")
    @PostMapping
    public Result<AppPackageRelationResp> add(@Validated @RequestBody AppPackageRelationAddReq req) {
        AppPackageRelationResp appPkgRelResp = appPkgRelService.add(RequestContext.currentOrgId(), RequestContext.currentUserId(), req);
        return Result.ok(appPkgRelResp);
    }

    @ApiOperation(value="删除应用包关联信息", notes="删除应用包关联信息")
    @DeleteMapping
    public Result<?> del(@Validated @RequestBody AppPackageRelationUpdateReq req) {
        appPkgRelService.del(RequestContext.currentOrgId(), RequestContext.currentUserId(), req);
        return Result.ok();
    }

}
