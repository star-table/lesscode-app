package com.polaris.lesscode.app.internal.api;

import com.polaris.lesscode.app.internal.req.CreateViewReq;
import com.polaris.lesscode.app.internal.resp.AppViewResp;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * App视图 （内部调用）
 *
 * @author roamer
 * @version v1.0
 * @date 2021/1/26 10:53
 */
@Api(tags = "App视图 (内部调用)")
@RequestMapping("/app/inner/api/v1/apps/views")
public interface AppViewApi {

    /**
     * 获取应用视图列表
     *
     * @param orgId
     * @param appId
     * @param includePublic
     * @param owner
     * @return
     */
    @ApiOperation(value = "获取应用视图列表", notes = "获取应用视图列表")
    @GetMapping()
    Result<List<AppViewResp>> getAppViewIdList(
            @ApiParam("组织ID") @RequestParam("orgId") Long orgId,
            @ApiParam("AppID") @RequestParam("appId") Long appId,
            @ApiParam("包含公共视图，默认不包含") @RequestParam(value = "includePublic", required = false, defaultValue = "false") Boolean includePublic,
            @ApiParam("视图所有者,为0或不传递均不查询私有视图") @RequestParam(value = "owner", required = false) Long owner);


    @ApiOperation("批量创建应用视图")
    @PostMapping("/batch")
    Result<List<AppViewResp>> createAppViews(@RequestBody List<CreateViewReq> reqs);
}
