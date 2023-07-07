package com.polaris.lesscode.app.controller;

import com.polaris.lesscode.app.entity.AppRelation;
import com.polaris.lesscode.app.req.AddAppMemberReq;
import com.polaris.lesscode.app.req.DelAppMemberReq;
import com.polaris.lesscode.app.resp.AppMemberResp;
import com.polaris.lesscode.app.service.AppMemberService;
import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags="应用成员管理")
@RestController
@RequestMapping("/app/api/v1/apps/{appId}/members")
public class AppMemberController {

    @Autowired
    private AppMemberService appMemberService;

    @ApiOperation("添加应用成员")
    @PostMapping
    public Result<List<AppRelation>> addMember(@PathVariable("appId") Long appId, @RequestBody AddAppMemberReq req){
        return Result.ok(appMemberService.addMember(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, req));
    }

    @ApiOperation("删除应用成员")
    @DeleteMapping
    public Result<Boolean> deleteMember(@PathVariable("appId") Long appId, @RequestBody DelAppMemberReq req){
        return Result.ok(appMemberService.delMember(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, req));
    }

    @ApiOperation("获取应用成员")
    @GetMapping
    public Result<List<AppMemberResp>> getMembers(
            @PathVariable("appId") Long appId,
            @ApiParam("是否查看所有的用户（将会只显示用户）") @RequestParam(value="allUser", required = false) boolean allUser){
        return Result.ok(appMemberService.getMembers(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, allUser));
    }
}
