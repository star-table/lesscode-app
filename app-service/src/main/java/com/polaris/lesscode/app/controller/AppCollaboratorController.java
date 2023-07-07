package com.polaris.lesscode.app.controller;

import com.polaris.lesscode.app.resp.AppMemberResp;
import com.polaris.lesscode.app.service.AppCollaboratorService;
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
@RequestMapping("/app/api/v1/apps/{appId}/collaborators")
public class AppCollaboratorController {

//    @Autowired
//    private AppCollaboratorService appCollaboratorService;
//    @ApiOperation("获取应用协作人")
//    @GetMapping
//    public Result<List<AppMemberResp>> getMembers(
//            @PathVariable("appId") Long appId,
//            @RequestParam("tableId") Long tableId,
//            @ApiParam("是否查看所有的用户（将会只显示用户）") @RequestParam(value="allUser", required = false) boolean allUser){
//        return Result.ok(appCollaboratorService.getCollaborators(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, tableId, allUser));
//    }
}
