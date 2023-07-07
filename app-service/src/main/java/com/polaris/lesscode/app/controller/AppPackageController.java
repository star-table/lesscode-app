package com.polaris.lesscode.app.controller;

import com.polaris.lesscode.app.req.AppPackageAddReq;
import com.polaris.lesscode.app.req.AppPackageUpdateReq;
import com.polaris.lesscode.app.req.MoveAppPackageReq;
import com.polaris.lesscode.app.resp.AppPackageListResp;
import com.polaris.lesscode.app.resp.AppPackageResp;
import com.polaris.lesscode.app.service.AppPackageService;
import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-08-03 11:14 上午
 */
@Api(tags="应用包管理")
@RestController
@Slf4j
@RequestMapping("/app/api/v1/app/packages")
public class AppPackageController {

    @Autowired
    private AppPackageService appPackageService;

    @ApiOperation(value="获取应用包和应用列表", notes="获取应用包和应用列表")
    @GetMapping
    public Result<AppPackageListResp> list() {
        AppPackageListResp appPackageRespList = appPackageService.packageRespList(RequestContext.currentOrgId(), RequestContext.currentUserId());
        return Result.ok(appPackageRespList);
    }

    @ApiOperation(value="获取应用包信息", notes="获取应用包信息")
    @GetMapping("/{pkdId}")
    public Result<AppPackageResp> getApp(@PathVariable("pkdId") Long pkgId) {
        AppPackageResp appPackageResp = appPackageService.get(pkgId, RequestContext.currentOrgId(), RequestContext.currentUserId());
        return Result.ok(appPackageResp);
    }

    @ApiOperation(value="添加应用包", notes="添加应用包")
    @PostMapping
    public Result<AppPackageResp> add(@Validated  @RequestBody AppPackageAddReq req) {
        AppPackageResp appPackageResp = appPackageService.addAppPackage(RequestContext.currentOrgId(), RequestContext.currentUserId(), req);
        return Result.ok(appPackageResp);
    }

    @ApiOperation(value="编辑应用包", notes="编辑应用包")
    @PostMapping("/{pkgId}")
    public Result<AppPackageResp> update(@PathVariable("pkgId") Long pkgId, @Validated @RequestBody AppPackageUpdateReq req) {
        AppPackageResp appPackageResp = appPackageService.modifyAppPackage(pkgId, RequestContext.currentOrgId(), RequestContext.currentUserId(), req);
        return Result.ok(appPackageResp);
    }

    @ApiOperation(value="删除应用包", notes="删除应用包")
    @DeleteMapping("/{pkgId}")
    public Result delete(@PathVariable("pkgId") Long pkgId) {
        appPackageService.deleteAppPackage(pkgId, RequestContext.currentOrgId(), RequestContext.currentUserId());
        return Result.ok();
    }

    @ApiOperation(value="移动应用包分组信息", notes="移动应用包分组信息")
    @PatchMapping
    public Result<?> move(@RequestBody MoveAppPackageReq req){
        appPackageService.moveAppPkg(RequestContext.currentOrgId(), RequestContext.currentUserId(), req);
        return Result.ok();
    }

}
