package com.polaris.lesscode.app.internal.api;

import com.polaris.lesscode.app.internal.req.*;
import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.app.internal.resp.TaskResp;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 应用服务
 *
 * @date 2020年9月10日
 */
@Api(value="应用管理（内部调用）", tags={"应用管理（内部调用）"})
@RequestMapping("/app/inner/api/v1/apps")
public interface AppApi {

    @ApiOperation(value="获取应用列表", notes="获取应用列表")
    @GetMapping("/get-app-list")
    Result<List<AppResp>> getAppList(
            @RequestParam(value = "orgId") Long orgId,
            @RequestParam(value = "pkgId", required = false) Long pkgId,
            @RequestParam(value = "type", required = false) Integer type);
    
    @ApiOperation(value="获取应用信息", notes="获取应用信息")
	@GetMapping("/get-app-info")
	Result<AppResp> getAppInfo(@RequestParam("orgId") Long orgId, @RequestParam("appId") Long appId);

    @ApiOperation(value="获取应用列表信息", notes="获取应用列表信息")
    @GetMapping("/get-app-info-list")
    Result<List<AppResp>> getAppInfoList(@RequestParam("orgId") Long orgId, @RequestParam("appIds") Collection<Long> appIds);

    @ApiOperation(value = "启动流程", tags = {"启动流程"})
    @GetMapping("/start-process")
    Result<TaskResp> startProcess(
            @RequestParam(value = "appId") Long appId,
            @RequestParam(value = "dataId") String dataId,
            @RequestParam(value = "userId") Long userId
    );

    @ApiOperation(value="创建应用", notes="创建应用")
    @PostMapping
    Result<AppResp> createApp(@RequestBody CreateAppReq req);

    @PutMapping
    Result<Boolean> updateApp(@RequestBody UpdateAppReq req);

    @DeleteMapping
    Result<Boolean> deleteApp(@RequestBody DeleteAppReq req);

    @ApiOperation(value="获取权限继承的顶级应用", notes="获取权限继承的顶级应用")
    @GetMapping("/auth-extends-app")
    Result<AppResp> getAuthExtendsApp(@RequestParam("appId") Long appId);

    @ApiOperation("添加项目成员")
    @PutMapping("/add-project-relation")
    Result<Boolean> addProjectMember(@RequestParam("appId") Long appId, @RequestParam("relationType") Integer relationType, @RequestParam("relationId") Long relationId);

    @ApiOperation("删除项目成员")
    @PutMapping("/remove-project-relation")
    Result<Boolean> removeProjectMember(@RequestParam("appId") Long appId, @RequestParam("relationType") Integer relationType, @RequestParam("relationId") Long relationId);

    @ApiOperation("判断是否为应用成员")
    @PutMapping("/is-project-member")
    Result<Boolean> isProjectMember(@RequestParam("appId") Long appId, @RequestParam("orgId") Long orgId, @RequestParam("userId") Long userId);

    @ApiOperation("判断是否为应用成员-批量")
    @PostMapping("/is-project-member-batch")
    Result<Map<Long, Boolean>> isProjectMemberBatch(@RequestBody IsProjectMemberBatchReq req);

    @ApiOperation("添加应用成员")
    @PutMapping("/add-app-relation")
    Result<Boolean> addAppMember(@RequestBody AddAppMemberInternalReq req);

    @ApiOperation("获取模板应用的id列表")
    @GetMapping("/get-template-ids")
    Result<List<Long>> getTemplateIds(@RequestParam("orgId") Long orgId);

    @ApiOperation("应用模板")
    @PostMapping("/apply-template")
    Result<List<Long>> applyTemplate(@RequestParam("orgId") Long orgId, @RequestParam("userId") Long userId, @RequestParam("templateId") Long templateId, @RequestParam("isNewbieGuide") boolean isNewbieGuide);
}
