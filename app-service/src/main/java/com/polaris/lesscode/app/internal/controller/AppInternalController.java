package com.polaris.lesscode.app.internal.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.polaris.lesscode.app.entity.App;
import com.polaris.lesscode.app.entity.Project;
import com.polaris.lesscode.app.entity.ProjectRelation;
import com.polaris.lesscode.app.internal.api.AppApi;
import com.polaris.lesscode.app.internal.enums.AppType;
import com.polaris.lesscode.app.internal.req.*;
import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.app.internal.resp.TaskResp;
import com.polaris.lesscode.app.internal.service.AppInternalService;
import com.polaris.lesscode.app.mapper.AppMapper;
import com.polaris.lesscode.app.mapper.ProjectMapper;
import com.polaris.lesscode.app.service.AppMemberService;
import com.polaris.lesscode.app.service.AppStarService;
import com.polaris.lesscode.app.service.AppTemplateService;
import com.polaris.lesscode.app.vo.ResultCode;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class AppInternalController implements AppApi{

    @Autowired
    private AppInternalService appInternalService;

    @Autowired
    private AppStarService appStarService;

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private AppMemberService appMemberService;

    @Autowired
    private AppTemplateService appTemplateService;

    @ApiOperation(value="获取应用列表", notes="获取应用列表")
    public Result<List<AppResp>> getAppList(
            Long orgId,
            Long pkgId,
            Integer type) {
        List<AppResp> list = appInternalService.list(orgId , pkgId, type);
        return Result.ok(list);
    }

    @ApiOperation(value="获取应用信息", notes="获取应用信息")
	public Result<AppResp> getAppInfo(Long orgId, Long appId) {
		AppResp resp = appInternalService.get(orgId, appId);
		return Result.ok(resp);
	}

    @ApiOperation(value="获取应用列表信息", notes="获取应用列表信息")
    public Result<List<AppResp>> getAppInfoList(Long orgId, Collection<Long> appIds) {
        return Result.ok(appInternalService.getList(orgId, appIds));
    }

    @Override
    @ApiOperation(value="启动流程", notes="启动流程")
    public Result<TaskResp> startProcess(Long appId, String dataId, Long userId) {
        return Result.ok(appInternalService.startProcess(appId, dataId, userId));
    }

    @Override
    public Result<AppResp> createApp(CreateAppReq req) {
        return Result.ok(appInternalService.createApp(req));
    }

    @Override
    public Result<Boolean> updateApp(UpdateAppReq req) {
        return Result.ok(appInternalService.updateApp(req));
    }

    @Override
    public Result<Boolean> deleteApp(DeleteAppReq req) {
        return Result.ok(appInternalService.deleteApp(req));
    }

    @Override
    public Result<AppResp> getAuthExtendsApp(Long appId) {
        return Result.ok(appInternalService.getAuthExtendsApp(appId));
    }

    @Override
    public Result<Boolean> addProjectMember(Long appId, Integer relationType, Long relationId) {
        App app = appMapper.get(appId);
        if (app != null && Objects.equals(app.getType(), AppType.PROJECT.getCode())){
            Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getAppId, app.getId()).last("limit 1"));
            if (project == null){
                throw new BusinessException(ResultCode.PROJECT_NOT_EXIST);
            }
            ProjectRelation projectRelation = new ProjectRelation();
            projectRelation.setOrgId(app.getOrgId());
            projectRelation.setProjectId(project.getId());
            projectRelation.setRelationType(relationType);
            projectRelation.setRelationId(relationId);
            appStarService.addRelation(projectRelation);
        }
        return Result.ok(true);
    }

    @Override
    public Result<Boolean> removeProjectMember(Long appId, Integer relationType, Long relationId) {
        App app = appMapper.get(appId);
        if (app != null && Objects.equals(app.getType(), AppType.PROJECT.getCode())){
            Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getAppId, app.getId()).last("limit 1"));
            if (project == null){
                throw new BusinessException(ResultCode.PROJECT_NOT_EXIST);
            }
            ProjectRelation projectRelation = new ProjectRelation();
            projectRelation.setOrgId(app.getOrgId());
            projectRelation.setProjectId(project.getId());
            projectRelation.setRelationType(relationType);
            projectRelation.setRelationId(relationId);
            appStarService.deleteRelation(0L, projectRelation);
        }
        return Result.ok(true);
    }

    @Override
    public Result<Boolean> isProjectMember(Long appId, Long orgId, Long userId) {
        return Result.ok(appMemberService.isProjectMember(orgId, appId, userId));
    }

    @Override
    public Result<Map<Long, Boolean>> isProjectMemberBatch(IsProjectMemberBatchReq req) {
        return Result.ok(appMemberService.isProjectMemberBatch(req));
    }

    @Override
    public Result<Boolean> addAppMember(@RequestBody AddAppMemberInternalReq req) {
        return Result.ok(appMemberService.addAppMember(req.getAppId(), req.getRelationType(), req.getRelationId()));
    }

    @Override
    public Result<List<Long>> getTemplateIds(Long orgId) {
        return Result.ok(appInternalService.getTemplateIds(orgId));
    }

    @Override
    public Result<List<Long>> applyTemplate(Long orgId, Long userId, Long templateId, boolean isNewbieGuide) {
        return Result.ok(appInternalService.applyTemplate(orgId, userId, templateId, isNewbieGuide));
    }

}
