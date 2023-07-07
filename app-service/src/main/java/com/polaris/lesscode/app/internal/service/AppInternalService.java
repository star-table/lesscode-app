package com.polaris.lesscode.app.internal.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.polaris.lesscode.app.bo.Event;
import com.polaris.lesscode.app.consts.AppConsts;
import com.polaris.lesscode.app.consts.ThreadPools;
import com.polaris.lesscode.app.entity.App;
import com.polaris.lesscode.app.entity.AppRelation;
import com.polaris.lesscode.app.entity.AppView;
import com.polaris.lesscode.app.entity.Project;
import com.polaris.lesscode.app.internal.enums.AppRelationType;
import com.polaris.lesscode.app.internal.enums.AppType;
import com.polaris.lesscode.app.internal.req.CreateAppReq;
import com.polaris.lesscode.app.internal.req.DeleteAppReq;
import com.polaris.lesscode.app.internal.req.UpdateAppReq;
import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.app.internal.resp.TaskResp;
import com.polaris.lesscode.app.mapper.AppMapper;
import com.polaris.lesscode.app.mapper.AppRelationMapper;
import com.polaris.lesscode.app.mapper.AppViewMapper;
import com.polaris.lesscode.app.mapper.ProjectMapper;
import com.polaris.lesscode.app.service.AppMemberService;
import com.polaris.lesscode.app.service.AppTemplateService;
import com.polaris.lesscode.app.service.GoPushService;
import com.polaris.lesscode.app.service.GroupService;
import com.polaris.lesscode.app.utils.PermissionUtil;
import com.polaris.lesscode.app.vo.ResultCode;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.dashboard.internal.req.DashboardCreate;
import com.polaris.lesscode.dashboard.internal.resp.DashboardResp;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.internal.sula.FormJson;
import com.polaris.lesscode.gotable.internal.feign.GoTableProvider;
import com.polaris.lesscode.gotable.internal.req.CreateSummeryTableRequest;
import com.polaris.lesscode.gotable.internal.req.CreateTableRequest;
import com.polaris.lesscode.permission.internal.enums.AppPerDefaultGroupLangCode;
import com.polaris.lesscode.permission.internal.enums.DefaultAppPermissionGroupType;
import com.polaris.lesscode.permission.internal.model.req.AddAppMembersReq;
import com.polaris.lesscode.permission.internal.model.req.InitAppPermissionReq;
import com.polaris.lesscode.permission.internal.model.resp.AppPerGroupListItem;
import com.polaris.lesscode.project.internal.api.ProjectApi;
import com.polaris.lesscode.project.internal.req.AppEvent;
import com.polaris.lesscode.util.ConvertUtil;
import com.polaris.lesscode.util.RpcUtil;
import edp.davinci.internal.feign.DashboardProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppInternalService extends ServiceImpl<AppMapper, App> {

	@Autowired
	private AppMapper appMapper;

//	@Autowired
//	private AppFormProvider formProvider;

	@Autowired
	private GoTableProvider goTableProvider;

	@Autowired
	private AppMemberService appRelationService;

	@Autowired
	private DashboardProvider dashboardProvider;

	@Autowired
	private PermissionUtil permissionUtil;

	@Autowired
	private GroupService groupService;

	@Autowired
	private AppViewMapper appViewMapper;

	@Autowired
	private ProjectMapper projectMapper;

	@Autowired
	private AppRelationMapper appRelationMapper;

	@Autowired
	private GoPushService goPushService;

	@Autowired
	private ProjectApi projectApi;

	@Autowired
	private AppTemplateService appTemplateService;

	public List<AppResp> list(Long orgId, Long pkgId, Integer type) {
		List<AppResp> resps = new ArrayList<>();
		LambdaQueryWrapper<App> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(App :: getDelFlag, CommonConsts.FALSE);
        queryWrapper.eq(App :: getOrgId, orgId);
        if(pkgId != null) {
			queryWrapper.eq(App :: getPkgId, pkgId);
        }
        if(type != null) {
        	queryWrapper.eq(App :: getType, type);
        }
        long start = System.currentTimeMillis();
        List<App> list = appMapper.selectList(queryWrapper);
        long end = System.currentTimeMillis();
        log.info("appMapper.selectList spend: {}, size: {}", end - start, list.size());
        resps = ConvertUtil.convertList(list, AppResp.class);
        return resps;
	}

	public AppResp get(Long orgId, Long appId) {
		App app = appMapper.get(orgId, appId);
		if(app == null) {
			throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " org:" + orgId + " app:" + appId);
		}
		return ConvertUtil.convert(app, AppResp.class);
	}

	public Boolean updateApp(UpdateAppReq req){
		App updated = new App();
		updated.setId(req.getAppId());
		updated.setName(req.getName());
		updated.setUpdator(req.getUserId());
		appMapper.updateById(updated);

		// 事件上报
		ThreadPools.POOLS.execute(() -> {
			App app = appMapper.get(req.getAppId());
			if (app == null) {
				throw new BusinessException(ResultCode.APP_NOT_EXIST);
			}
			AppEvent appEvent = new AppEvent();
			appEvent.setOrgId(req.getOrgId());
			appEvent.setAppId(req.getAppId());
			appEvent.setProjectId(app.getProjectId());
			appEvent.setUserId(req.getUserId());
			if (app.getProjectId() > 0) {
				Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getId, app.getProjectId()).last("limit 1"));
				if (project == null) {
					throw new BusinessException(ResultCode.PROJECT_NOT_EXIST);
				}
				appEvent.setProject(project);
			}
			List<AppRelation> appRelations = appRelationMapper.selectList(new LambdaQueryWrapper<AppRelation>()
					.eq(AppRelation::getOrgId, req.getOrgId())
					.eq(AppRelation::getDelFlag, CommonConsts.FALSE)
					.eq(AppRelation::getAppId, req.getAppId())
					.eq(AppRelation::getType, AppRelationType.STAR.getCode())
					.eq(AppRelation::getRelationId, req.getUserId())
			);
			app.setHasStared(appRelations.size() > 0);
			appEvent.setApp(app);
			projectApi.reportAppEvent(AppConsts.EventTypeAppRefresh, "", appEvent);
		});

		return true;
	}

	public Boolean deleteApp(DeleteAppReq req){
		App app = appMapper.get(req.getAppId());
		if (app == null) {
			return true;
		}

		App updated = new App();
		updated.setId(req.getAppId());
		updated.setDelFlag(CommonConsts.TRUE);
		updated.setUpdator(req.getUserId());
		appMapper.updateById(updated);
		appViewMapper.update(null, new LambdaUpdateWrapper<AppView>().eq(AppView::getAppId, req.getAppId()).eq(AppView::getDelFlag, CommonConsts.FALSE).set(AppView::getDelFlag, CommonConsts.TRUE));

		// 事件上报
		ThreadPools.POOLS.execute(() -> {
			AppEvent appEvent = new AppEvent();
			appEvent.setOrgId(req.getOrgId());
			appEvent.setAppId(req.getAppId());
			appEvent.setProjectId(app.getProjectId());
			appEvent.setUserId(req.getUserId());
			projectApi.reportAppEvent(AppConsts.EventTypeAppDeleted, "", appEvent);
		});

		return true;
	}

	public AppResp createApp(CreateAppReq req){
		App app = new App();
		app.setOrgId(req.getOrgId());
		app.setName(req.getName());
		app.setType(req.getAppType());
		app.setCreator(req.getUserId());
		app.setPkgId(req.getPkgId());
		app.setUpdator(req.getUserId());
		app.setExtendsId(req.getExtendsId());
		app.setProjectId(req.getProjectId());
		app.setParentId(req.getParentId());
		app.setSort(groupService.getMinSort(req.getOrgId()) - 1);
		app.setHidden(req.getHidden());
//		app.setAuthType(req.getAuthType());
		app.setAuthType(2); // 默认都不继承
		app.setIcon(req.getIcon());
		app.setExternalApp(req.getExternalApp());
		app.setLinkUrl(req.getLinkUrl());
		app.setMirrorAppId(req.getMirrorAppId());
		app.setMirrorViewId(req.getMirrorViewId());
		if (Objects.isNull(req.getPkgId())){
			req.setPkgId(0L);
		}
		if (AppType.PROJECT.getCode().equals(app.getType())){
			req.setAppType(2);
		}
		if (appMapper.insert(app) < 1){
			throw new BusinessException(ResultCode.APP_ADD_FAIL);
		}
		if (Objects.equals(req.getAddAllMember(), true)){
			AppRelation appRelation = new AppRelation();
			appRelation.setAppId(app.getId());
			appRelation.setRelationId(0L);
			appRelation.setType(AppRelationType.DEPT.getCode());
			appRelation.setOrgId(app.getOrgId());
			appRelation.setCreator(app.getCreator());
			appRelation.setUpdator(app.getUpdator());
			appRelationMapper.insert(appRelation);
		}
		// 添加创建人为成员
		if (req.getUserId() > 0) {
			AppRelation appRelation = new AppRelation();
			appRelation.setAppId(app.getId());
			appRelation.setRelationId(req.getUserId());
			appRelation.setType(AppRelationType.USER.getCode());
			appRelation.setOrgId(app.getOrgId());
			appRelation.setCreator(app.getCreator());
			appRelation.setUpdator(app.getUpdator());
			appRelationMapper.insert(appRelation);
		}
		AppResp appResp = ConvertUtil.convert(app, AppResp.class);

		InitAppPermissionReq initPerReq = new InitAppPermissionReq();
		FormJson formJson = new FormJson("create");
		if (AppType.DASHBOARD.getCode().equals(app.getType())) {
			DashboardCreate dashboardCreate = new DashboardCreate();
			dashboardCreate.setOrgId(req.getOrgId());
			dashboardCreate.setAppId(app.getId());
			dashboardCreate.setName(app.getName());
			dashboardCreate.setType((short) 1);
			DashboardResp resp = RpcUtil.accept(dashboardProvider.createDashboard(dashboardCreate));
			initPerReq.setDefaultPermissionGroupType(DefaultAppPermissionGroupType.POLARIS_PROJECT.getCode());
			log.info("创建仪表盘成功： {}", JSON.toJSONString(resp));

			appResp.setDashboardId(resp.getId());
		} else if(AppType.FORM.getCode().equals(app.getType())
				|| AppType.FOLDER.getCode().equals(app.getType())
				|| AppType.PACKAGE.getCode().equals(app.getType())
				|| AppType.SUMMARY.getCode().equals(app.getType())
				|| AppType.PROJECT.getCode().equals(app.getType())) {
			if (StringUtils.isBlank(req.getConfig())){
				req.setConfig("{}");
			}
			if (AppType.PROJECT.getCode().equals(app.getType())){
				initPerReq.setDefaultPermissionGroupType(DefaultAppPermissionGroupType.POLARIS_PROJECT.getCode());
			} else {
				initPerReq.setDefaultPermissionGroupType(DefaultAppPermissionGroupType.FORM.getCode());
			}
		} else if (AppType.MIRROR.getCode().equals(app.getType())) {
			initPerReq.setDefaultPermissionGroupType(DefaultAppPermissionGroupType.POLARIS_PROJECT.getCode());
		} else {
			throw new BusinessException(ResultCode.UNSUPPORT_APP_TYPE);
		}
		initPerReq.setOrgId(req.getOrgId());
		initPerReq.setAppId(app.getId());
		initPerReq.setUserId(req.getUserId());
		initPerReq.setAppType(app.getType());
//		initPerReq.setConfig(JSON.toJSONString(formJson));
		boolean suc = permissionUtil.initDefaultPermissionGroup(initPerReq).getData();
		if(! suc) {
			throw new BusinessException(ResultCode.INIT_FORM_PERMISSION_GROUP_FAIL);
		}

		// 设置极星项目创建人，默认是管理员角色
		List<AppPerGroupListItem> perGroups = permissionUtil.getAppPermissionGroupList(req.getOrgId(), app.getId());
		log.info("[createApp] 获取权限组: {}", perGroups);
		for (int i = 0; i < perGroups.size(); i++) {
			AppPerGroupListItem perGroup = perGroups.get(i);
			if (perGroup.getLangCode().equals(AppPerDefaultGroupLangCode.OWNER.getCode()) ||
					perGroup.getLangCode().equals(AppPerDefaultGroupLangCode.FORM_ADMINISTRATOR.getCode()) ||
					perGroup.getLangCode().equals(AppPerDefaultGroupLangCode.DASHBOARD_ADMINISTRATOR.getCode())) {
				List<String> memberIds = new ArrayList<>();
				memberIds.add(AppConsts.MEMBER_USER_TYPE + req.getUserId().toString());
				AddAppMembersReq addAppMembersReq = new AddAppMembersReq();
				addAppMembersReq.setOrgId(req.getOrgId());
				addAppMembersReq.setAppId(app.getId());
				addAppMembersReq.setMemberIds(memberIds);
				addAppMembersReq.setPerGroupId(perGroup.getId());
				log.info("[createApp] 添加创建人到权限组: {}", addAppMembersReq);
				suc = permissionUtil.addAppMembers(addAppMembersReq);
				if(! suc) {
					throw new BusinessException(ResultCode.INIT_APP_CREATOR_PERMISSION_GROUP_FAIL);
				}
				break;
			}
		}

		FormJson currentForm = JSON.parseObject(req.getConfig(), FormJson.class);
		// 目前就这三个去创建表，其他都不创建了
		if (AppType.SUMMARY.getCode().equals(app.getType())){
			goTableProvider.createSummery(new CreateSummeryTableRequest(app.getId(),currentForm.getFields()),
					req.getOrgId().toString(), req.getUserId().toString());
		} else if (AppType.FORM.getCode().equals(app.getType())){
			goTableProvider.create(new CreateTableRequest(app.getId(),"项目表单",null,null,
					true, true, app.getType(),currentForm.getFields()),req.getOrgId().toString(), req.getUserId().toString());
		} else if (AppType.PROJECT.getCode().equals(app.getType())){
			short widgetFlag = 1;
			String tableName = "任务";
			if (AppConsts.PROJECT_TYPE_EMPTY.equals(req.getProjectType())) {
				widgetFlag = 2;
				tableName = "表";
			}
			// 这里有个问题，如果后续需要创建3个表的话，可能需要极星那边去调用了，目前先这样。
			goTableProvider.create(new CreateTableRequest(app.getId(),tableName,currentForm.getBaseFields(),currentForm.getNotNeedSummeryColumnIds(),
					false,true,app.getType(),currentForm.getFields()),req.getOrgId().toString(), req.getUserId().toString());

			log.info("创建表成功： {}, {}", tableName, widgetFlag);

			DashboardCreate dashboardCreate = new DashboardCreate();
			dashboardCreate.setOrgId(req.getOrgId());
			dashboardCreate.setAppId(app.getId());
			dashboardCreate.setName("仪表盘");
			dashboardCreate.setType((short) 1);
			dashboardCreate.setWidgetFlag(widgetFlag); // 1代表则需要系统widget
			DashboardResp resp = dashboardProvider.createDashboard(dashboardCreate).getData();
			log.info("创建仪表盘成功： {}", JSON.toJSONString(resp));
			appResp.setDashboardId(resp.getId());
		}

		// MQTT推送
		ThreadPools.POOLS.execute(() -> {
			try {
				Thread.sleep(1000L); // 等待创建事务完成
			} catch (InterruptedException e) {
			}
			AppEvent appEvent = new AppEvent();
			appEvent.setOrgId(req.getOrgId());
			appEvent.setAppId(app.getId());
			appEvent.setProjectId(app.getProjectId());
			appEvent.setUserId(req.getUserId());
			if (app.getProjectId() > 0) {
				Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getId, app.getProjectId()).last("limit 1"));
				if (project == null) {
					throw new BusinessException(ResultCode.PROJECT_NOT_EXIST);
				}
				appEvent.setProject(project);
			}
			List<AppRelation> appRelations = appRelationMapper.selectList(new LambdaQueryWrapper<AppRelation>()
					.eq(AppRelation::getOrgId, req.getOrgId())
					.eq(AppRelation::getDelFlag, CommonConsts.FALSE)
					.eq(AppRelation::getAppId, app.getId())
					.eq(AppRelation::getType, AppRelationType.STAR.getCode())
					.eq(AppRelation::getRelationId, req.getUserId())
			);
			app.setHasStared(appRelations.size() > 0);
			appEvent.setApp(app);
			projectApi.reportAppEvent(AppConsts.EventTypeAppRefresh, "", appEvent);
		});

		return appResp;
	}

	public List<AppResp> getList(Long orgId, Collection<Long> appIds) {
		List<App> apps = appMapper.getListByIds(orgId, appIds);
		return ConvertUtil.convertList(apps, AppResp.class);
	}

	public TaskResp startProcess(Long appId, String dataId, Long userId){
		throw new BusinessException(ResultCode.APP_PROCESS_START_FAIL);
	}

	public AppResp getAuthExtendsApp(Long appId){
		App app = appMapper.get(appId);
		if (app == null){
			throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " app:" + appId);
		}
		App targetApp = appRelationService.getExtendsAuthApp(app);
		return ConvertUtil.convert(targetApp, AppResp.class);
	}

	public List<Long> getTemplateIds(Long orgId){
		List<App> apps = appMapper.selectList(new LambdaQueryWrapper<App>()
				.eq(App::getOrgId, orgId)
				.eq(App::getDelFlag, CommonConsts.FALSE)
				.eq(App::getTemplateFlag, CommonConsts.TRUE));
		if (CollectionUtils.isEmpty(apps)){
			return new ArrayList<>();
		}
		return apps.stream().map(App::getId).collect(Collectors.toList());
	}

	public List<Long> applyTemplate(Long orgId, Long userId, Long templateId, boolean isNewbieGuide) {
		List<com.polaris.lesscode.app.resp.AppResp> appRespList = appTemplateService.apply(orgId, userId, templateId, Long.valueOf(0), false, 0, isNewbieGuide);
		ArrayList<Long> appIds = new ArrayList<>();
		appRespList.forEach(appResp -> {
			appIds.add(appResp.getId());
		});
		return appIds;
	}
}
