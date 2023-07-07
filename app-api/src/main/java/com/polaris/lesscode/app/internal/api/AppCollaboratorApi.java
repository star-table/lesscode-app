package com.polaris.lesscode.app.internal.api;

import com.polaris.lesscode.app.internal.req.UpdateCollaboratorReq;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value="应用管理（内部调用）", tags={"应用管理（内部调用）"})
@RequestMapping("/app/inner/api/v1/apps/{appId}/collaborators")
public interface AppCollaboratorApi {

//    @ApiOperation("获取用户在应用的协作角色")
//    @PostMapping("getUserRoles")
//    Result<List<Long>> getUserCollaboratorRoleIds(@PathVariable("appId") Long appId, @RequestParam("orgId") Long orgId, @RequestParam("tableId") Long tableId, @RequestParam("userId") Long userId);
}
