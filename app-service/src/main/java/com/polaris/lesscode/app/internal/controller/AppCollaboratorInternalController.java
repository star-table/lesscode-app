package com.polaris.lesscode.app.internal.controller;

import com.polaris.lesscode.app.internal.api.AppCollaboratorApi;
import com.polaris.lesscode.app.internal.req.UpdateCollaboratorReq;
import com.polaris.lesscode.app.internal.service.AppCollaboratorInternalService;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AppCollaboratorInternalController implements AppCollaboratorApi {
    @Autowired
    private AppCollaboratorInternalService appCollaboratorInternalService;

    @ApiOperation("获取用户在应用的协作角色")
    @PostMapping("getUserRoles")
    public Result<List<Long>> getUserCollaboratorRoleIds(Long appId, Long orgId,Long tableId, Long userId) {
        return Result.ok(appCollaboratorInternalService.getUserCollaboratorRoleIds(orgId, appId, tableId, userId));
    }
}
