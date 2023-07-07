package com.polaris.lesscode.app.openapi.controller;

//import com.polaris.lesscode.app.openapi.req.AppOpenAddReq;
//import com.polaris.lesscode.app.openapi.req.AppOpenUpdateReq;
//import com.polaris.lesscode.app.openapi.service.AppOpenService;
//import com.polaris.lesscode.app.resp.AppResp;
//import com.polaris.lesscode.context.RequestContext;
//import com.polaris.lesscode.vo.Result;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Api(tags="应用管理（openApi）")
//@Slf4j
//@RestController
//@RequestMapping("open/app/api/v1/apps")
//public class AppOpenController {
//
//    @Autowired
//    private AppOpenService appOpenService;
//
//    @ApiOperation(value="添加应用", notes="添加应用")
//    @PostMapping
//    public Result<AppResp> add(@Validated @RequestBody AppOpenAddReq req) {
//        AppResp resp = appOpenService.add(req, RequestContext.currentOrgId(), RequestContext.currentUserId());
//        return Result.ok(resp);
//    }
//
//    @ApiOperation(value="编辑应用", notes="编辑应用")
//    @PostMapping("/{appId}")
//    public Result<AppResp> update(@PathVariable("appId") Long appId,@Validated @RequestBody AppOpenUpdateReq req) {
//        log.info(">>>>>>>>>>>>>>>>>>>>>openApi>>>>编辑应用>>>>>>>>>>>>>in>>>>appId={}>>req={}", appId, req);
//        AppResp resp = appOpenService.update(appId, req, RequestContext.currentOrgId(), RequestContext.currentUserId());
//        return Result.ok(resp);
//    }
//
//    @ApiOperation(value="删除应用", notes="删除应用")
//    @DeleteMapping("/{appId}")
//    public Result<Boolean> delete(@PathVariable("appId") Long appId) {
//        boolean suc = appOpenService.delete(appId, RequestContext.currentOrgId(), RequestContext.currentUserId());
//        return Result.ok(suc);
//    }
//
//}
