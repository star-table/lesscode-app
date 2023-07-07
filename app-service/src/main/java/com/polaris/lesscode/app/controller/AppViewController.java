package com.polaris.lesscode.app.controller;

import com.polaris.lesscode.app.req.AppViewAddReq;
import com.polaris.lesscode.app.req.AppViewEditReq;
import com.polaris.lesscode.app.req.AppViewSortReq;
import com.polaris.lesscode.app.resp.AppViewListResp;
import com.polaris.lesscode.app.resp.AppViewResp;
import com.polaris.lesscode.app.service.AppViewService;
import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * App视图控制器
 *
 * @author roamer
 * @version v1.0
 * @date 2021/1/22 14:00
 */
@Api(tags = "App视图")
@RestController
@RequestMapping("/app/api/v1/apps/{appId}/views")
public class AppViewController {

    @Autowired
    private AppViewService appViewService;

    @ApiOperation(value = "获取AppView列表", notes = "获取AppView列表")
    @GetMapping
    public Result<AppViewListResp> getAppViewList(@PathVariable("appId") Long appId,
                                                  @ApiParam("是否开启权限过滤,默认不开启") @RequestParam(value = "authFilter", required = false, defaultValue = "false") Boolean authFilter,
                                                  @ApiParam("是否包含私有视图,默认不包含") @RequestParam(value = "isPrivate", required = false, defaultValue = "false") Boolean isPrivate
    ) {
        return Result.ok(appViewService.getAppViewListResp(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, authFilter, isPrivate));
    }

    @ApiOperation("获取AppView详情")
    @GetMapping("{viewId}")
    public Result<AppViewResp> getAppView(@PathVariable("appId") Long appId, @PathVariable("viewId") Long viewId) {
        return Result.ok(appViewService.getAppViewResp(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, viewId));
    }

    @ApiOperation("创建AppView")
    @PostMapping()
    public Result<Long> createAppView(@PathVariable("appId") Long appId, @Validated @RequestBody AppViewAddReq appViewAddReq) {
        return Result.ok(appViewService.createAppView(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, appViewAddReq));
    }

    @ApiOperation("编辑AppView")
    @PutMapping("{viewId}")
    public Result<Boolean> editAppView(@PathVariable("appId") Long appId, @PathVariable("viewId") Long viewId, @Validated @RequestBody AppViewEditReq appViewEditReq) {
        return Result.ok(appViewService.editAppView(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, viewId, appViewEditReq));
    }

    @ApiOperation("删除AppView")
    @DeleteMapping("{viewId}")
    public Result<Boolean> deleteAppView(@PathVariable("appId") Long appId, @PathVariable("viewId") Long viewId) {
        return Result.ok(appViewService.deleteAppView(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, viewId));
    }

    @ApiOperation("视图排序")
    @PutMapping("{viewId}/sort")
    public Result<Boolean> sortAppView(@PathVariable("appId") Long appId, @PathVariable("viewId") Long viewId, @RequestBody AppViewSortReq req){
        return Result.ok(appViewService.sortAppView(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, viewId, req));
    }

}
