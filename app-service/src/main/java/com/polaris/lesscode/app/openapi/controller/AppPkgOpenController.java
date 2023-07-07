package com.polaris.lesscode.app.openapi.controller;

//import com.polaris.lesscode.app.openapi.req.AppPackageOpenAddReq;
//import com.polaris.lesscode.app.openapi.req.AppPackageOpenUpdateReq;
//import com.polaris.lesscode.app.openapi.service.AppPackageOpenService;
//import com.polaris.lesscode.app.resp.AppPackageResp;
//import com.polaris.lesscode.context.RequestContext;
//import com.polaris.lesscode.vo.Result;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Api(tags="应用包管理（openApi）")
//@RestController
//@RequestMapping("open/app/api/v1/app/packages")
//public class AppPkgOpenController {
//
//    @Autowired
//    private AppPackageOpenService appPkgOpenService;
//
//    @ApiOperation(value="添加应用包", notes="添加应用包")
//    @PostMapping
//    public Result<AppPackageResp> add(@Validated  @RequestBody AppPackageOpenAddReq req) {
//        AppPackageResp appPackageResp = appPkgOpenService.addAppPackage(RequestContext.currentOrgId(), RequestContext.currentUserId(), req);
//        return Result.ok(appPackageResp);
//    }
//
//    @ApiOperation(value="编辑应用包", notes="编辑应用包")
//    @PostMapping("/{pkgId}")
//    public Result<AppPackageResp> update(@PathVariable("pkgId") Long pkgId, @Validated @RequestBody AppPackageOpenUpdateReq req) {
//        AppPackageResp appPackageResp = appPkgOpenService.modifyAppPackage(pkgId, RequestContext.currentOrgId(), RequestContext.currentUserId(), req);
//        return Result.ok(appPackageResp);
//    }
//
//    @ApiOperation(value="删除应用包", notes="删除应用包")
//    @DeleteMapping("/{pkgId}")
//    public Result delete(@PathVariable("pkgId") Long pkgId) {
//        appPkgOpenService.deleteAppPackage(pkgId, RequestContext.currentOrgId(), RequestContext.currentUserId());
//        return Result.ok();
//    }
//
//}
