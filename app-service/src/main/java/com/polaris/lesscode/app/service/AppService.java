package com.polaris.lesscode.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.polaris.lesscode.app.bo.AfterMoveBo;
import com.polaris.lesscode.app.bo.Event;
import com.polaris.lesscode.app.consts.AppConsts;
import com.polaris.lesscode.app.consts.ThreadPools;
import com.polaris.lesscode.app.entity.*;
import com.polaris.lesscode.app.enums.AppStatus;
import com.polaris.lesscode.app.enums.AppTemplateType;
import com.polaris.lesscode.app.enums.ReferenceType;
import com.polaris.lesscode.app.enums.YesOrNo;
import com.polaris.lesscode.app.internal.enums.*;
import com.polaris.lesscode.app.mapper.*;
import com.polaris.lesscode.app.req.AppAddReq;
import com.polaris.lesscode.app.req.AppSwitchAuthTypeReq;
import com.polaris.lesscode.app.req.AppUpdateReq;
import com.polaris.lesscode.app.req.MoveAppReq;
import com.polaris.lesscode.app.resp.*;
import com.polaris.lesscode.app.utils.PermissionUtil;
import com.polaris.lesscode.app.vo.ResultCode;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.dashboard.internal.req.DashboardCreate;
import com.polaris.lesscode.dashboard.internal.resp.DashboardResp;
import com.polaris.lesscode.dc.internal.api.DataCenterApi;
import com.polaris.lesscode.dc.internal.dsl.*;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.internal.api.AppValueApi;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.req.AppFormExcelSaveReq;
import com.polaris.lesscode.form.internal.sula.Field;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.gotable.internal.feign.GoTableProvider;
import com.polaris.lesscode.gotable.internal.req.CreateSummeryTableRequest;
import com.polaris.lesscode.gotable.internal.req.CreateTableRequest;
import com.polaris.lesscode.permission.internal.api.PermissionApi;
import com.polaris.lesscode.permission.internal.enums.AppPerDefaultGroupLangCode;
import com.polaris.lesscode.permission.internal.enums.DefaultAppPermissionGroupType;
import com.polaris.lesscode.permission.internal.enums.OperateAuthCode;
import com.polaris.lesscode.permission.internal.feign.AppPermissionProvider;
import com.polaris.lesscode.permission.internal.model.OrgUserPermissionContext;
import com.polaris.lesscode.permission.internal.model.UserPermissionVO;
import com.polaris.lesscode.permission.internal.model.req.AddAppMembersReq;
import com.polaris.lesscode.permission.internal.model.req.InitAppPermissionReq;
import com.polaris.lesscode.permission.internal.model.resp.AppAuthorityResp;
import com.polaris.lesscode.permission.internal.model.resp.AppPerGroupListItem;
import com.polaris.lesscode.project.internal.api.ProjectApi;
import com.polaris.lesscode.project.internal.req.AppEvent;
import com.polaris.lesscode.uc.internal.api.UserCenterApi;
import com.polaris.lesscode.uc.internal.req.GetUserDeptIdsReq;
import com.polaris.lesscode.uc.internal.req.UserListByIdsReq;
import com.polaris.lesscode.uc.internal.resp.GetUserDeptIdsResp;
import com.polaris.lesscode.uc.internal.resp.UserInfoResp;
import com.polaris.lesscode.util.ConvertUtil;
import com.polaris.lesscode.util.DataSourceUtil;
import com.polaris.lesscode.util.FieldKeyNameUtils;
import com.polaris.lesscode.util.RpcUtil;
import com.polaris.lesscode.vo.Result;
import edp.davinci.internal.feign.DashboardProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static com.polaris.lesscode.consts.CommonConsts.DEFAULT_HEADER_CONFIG;

@Slf4j
@Service
public class AppService extends ServiceImpl<AppMapper, App> {

	@Autowired
	private AppVersionMapper appVersionMapper;

	@Autowired
	private AppMapper appMapper;

	@Autowired
	private AppPackageMapper appPackageMapper;

	@Autowired
	private GroupService groupService;

	@Autowired
	private GoTableProvider goTableProvider;

	@Autowired
	private AppPackageService appPackageService;

	@Autowired
	private DashboardProvider dashboardProvider;

	@Autowired
	private PermissionUtil permissionUtil;

	@Autowired
	private AppRelationMapper appRelationMapper;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private PermissionApi permissionApi;

	@Autowired
	private AppPermissionProvider permissionProvider;

	@Autowired
	private AppMemberService appRelationService;

	@Autowired
	private AppStarService appStarService;

	@Autowired
	private ProjectRelationMapper projectRelationMapper;

	@Autowired
	private ProjectMapper projectMapper;

	@Autowired
	private AppTemplateMapper appTemplateMapper;

	@Autowired
	private AppViewMapper appViewMapper;

	@Autowired
	private AppValueApi appValueApi;

	@Autowired
	private UserCenterApi userCenterApi;

	@Autowired
	private DataCenterApi dataCenterApi;

	@Autowired
	private GoPushService goPushService;

	@Autowired
	private ProjectApi projectApi;

	public AppListResp list(Long orgId, Long userId, Long parentId, Integer type, Integer workflowFlag, Integer starFlag, Long templateId, Boolean isNeedAllApps) {
		AppListResp resp = new AppListResp();
		resp.setAppListResp(new ArrayList<>());
		resp.setProjectListResp(new ArrayList<>());

		ArrayList<Long> projectIds = new ArrayList<>();

		LambdaQueryWrapper<App> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(App::getDelFlag, CommonConsts.FALSE).eq(App::getOrgId, orgId);
		if (type != null) {
			queryWrapper.eq(App::getType, type);
		}
		if (parentId != null) {
			queryWrapper.eq(App::getParentId, parentId);
		}
		if (workflowFlag != null) {
			queryWrapper.eq(App::getWorkflowFlag, workflowFlag);
		}

		List<Long> staredAppIds = new ArrayList<>();
		if (templateId != null){
			AppTemplate appTemplate = appTemplateMapper.selectOne(new LambdaQueryWrapper<AppTemplate>().eq(AppTemplate::getDelFlag, CommonConsts.FALSE).eq(AppTemplate::getId, templateId));
			if (appTemplate == null){
				throw new BusinessException(ResultCode.APP_TEMPLATE_NOT_EXIST);
			}
			if (Objects.equals(appTemplate.getType(), AppTemplateType.PRIVATE.getCode()) && ! Objects.equals(appTemplate.getOrgId(), orgId)){
				throw new BusinessException(ResultCode.APP_TEMPLATE_NOT_EXIST);
			}
			List<Long> sampleAppIds = JSON.parseArray(appTemplate.getConfig(), Long.class);
			if (CollectionUtils.isEmpty(sampleAppIds)){
				throw new BusinessException(ResultCode.APP_TEMPLATE_CONFIG_IS_EMPTY);
			}
			queryWrapper.in(App::getId, sampleAppIds);
		}else{
			queryWrapper.eq(App :: getTemplateFlag, CommonConsts.FALSE);
			staredAppIds = appStarService.getUserStarAppIds(orgId, userId);
			if (Objects.equals(starFlag, 1)){
				if (CollectionUtils.isEmpty(staredAppIds)){
					return resp;
				}
				queryWrapper.in(App::getId, staredAppIds);
			}
		}
		List<App> list = appMapper.selectList(queryWrapper);
		//获取我协作的数据(暂时只处理极星的)
		Long summaryAppId = 0L;
		for (App app: list){
			if (Objects.equals(app.getType(), AppType.SUMMARY.getCode()) && app.getProjectId() == -1) {
				summaryAppId = app.getId();
			}
			break;
		}
		if (isNeedAllApps == null) {
			UserPermissionVO userPermissionVO = permissionApi.getUserPermission(orgId, userId).getData();
			// 模板权限？
			if (Objects.isNull(templateId)) {
				// 做权限处理
				List<Long> hasViewAppIds = new ArrayList<>();
				if (CollectionUtils.isNotEmpty(list)
						&& ! userPermissionVO.getIsOrgOwner()
						&& ! userPermissionVO.getIsSysAdmin()
						&& ! (userPermissionVO.getIsSubAdmin() && userPermissionVO.getManageApps().contains(-1L))){
					Set<Long> hasSetViewAppIds = getUserVisibleAppIds(userPermissionVO);
					Set<Long> appListIds = list.stream().map(App::getId).collect(Collectors.toSet());
					if (!Objects.equals(summaryAppId, 0L)) {
						GetUserDeptIdsReq getUserDeptIdsReq = new GetUserDeptIdsReq();
						getUserDeptIdsReq.setOrgId(orgId);
						getUserDeptIdsReq.setUserId(userId);
						GetUserDeptIdsResp getUserDeptIdsResp = userCenterApi.getUserDeptIds(getUserDeptIdsReq).getData();
						Collection<String> allRelateMember = new ArrayList<>();
						allRelateMember.add("'" + AppConsts.MEMBER_USER_TYPE + userId + "'");
						getUserDeptIdsResp.getDeptIds().forEach(item->{
							allRelateMember.add("'" + AppConsts.MEMBER_DEPT_TYPE + item + "'");
						});

						// 获取数据
						Condition cond = Conditions.rawSql("\"orgId\"=" + orgId + " AND \"recycleFlag\"=2 AND collaborators && ARRAY[" + String.join(",", allRelateMember) + "]");
						List<Map<String, Object>> formDatas = dataCenterApi.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(),
								Query.select("\"appId\"").from(new Table("lc_data")).where(cond).group("\"appId\"")
						).getData();
						formDatas.forEach(item->{
							Object appIdObj = item.get("appId");
							Long aid = Long.valueOf(appIdObj.toString());
							if (appListIds.contains(aid)){
								hasSetViewAppIds.add(aid);
							}
						});
					}
					hasViewAppIds.addAll(getTheViewAppIds(list, hasSetViewAppIds));
				}
				if ( userPermissionVO.getIsSubAdmin() && !userPermissionVO.getManageApps().contains(-1L)) {
					// 普通管理员管理部分应用
					hasViewAppIds.addAll(userPermissionVO.getManageApps());
				}
				if (!userPermissionVO.getIsOrgOwner()
						&& !userPermissionVO.getIsSysAdmin()
						&& !(userPermissionVO.getIsSubAdmin() && (userPermissionVO.getManageApps().contains(-1L)))) {
					list.removeIf(app -> ! hasViewAppIds.contains(app.getId()));
					if (CollectionUtils.isEmpty(hasViewAppIds)){
						return resp;
					}
				}
			}
			if(! CollectionUtils.isEmpty(list)){
				list.forEach(app -> {app.setMirrorTypeId(app.getMirrorTableId());});
				List<AppResp> appListResp = ConvertUtil.convertList(list, AppResp.class);
				for (AppResp appResp: appListResp){
					if (userPermissionVO.getIsSysAdmin()
							|| Objects.equals(appResp.getCreator(), userId)
							|| userPermissionVO.getIsOrgOwner()
							|| userPermissionVO.getManageApps().contains(-1L)
							|| userPermissionVO.getManageApps().contains(appResp.getId())){
						appResp.setDeletable(true);
						appResp.setEditable(true);
						appResp.setHasExtDelete(true);
						appResp.setHasExtUpdate(true);
						appResp.setHasExtMove(true);
					}
					if (CollectionUtils.isNotEmpty(staredAppIds)){
						if (staredAppIds.contains(appResp.getId())){
							appResp.setHasStared(true);
						}
					}
				}
				// 北极星过滤汇总表和空项目，规则：类型为汇总表或者类型为项目且项目id为空
				appListResp.removeIf(app -> (Objects.equals(app.getType(), AppType.PROJECT.getCode()) && (app.getProjectId() == null || app.getProjectId() <= 0)));
				resp.setAppListResp(appListResp);
			}
		}else if (isNeedAllApps){
			List<AppResp> appListResp = ConvertUtil.convertList(list, AppResp.class);
			appListResp.removeIf(app -> (Objects.equals(app.getType(), AppType.PROJECT.getCode()) && (app.getProjectId() == null || app.getProjectId() <= 0)));
			resp.setAppListResp(appListResp);
		}
		if (CollectionUtils.isNotEmpty(resp.getAppListResp())){
			resp.getAppListResp().sort(new Comparator<AppResp>() {
				@Override
				public int compare(AppResp o1, AppResp o2) {
					return o1.getSort().compareTo(o2.getSort());
				}
			});
			resp.getAppListResp().forEach(app -> {
				if (Objects.equals(app.getType(), AppType.SUMMARY.getCode())){
					app.setProjectId((long) -1);
				}
			});
		}

		if (isNeedAllApps != null && isNeedAllApps) {
			return resp;
		}

		for (AppResp app: resp.getAppListResp()) {
			if (Objects.equals(app.getType(), AppType.PROJECT.getCode()) && app.getProjectId() > 0) {
				projectIds.add(app.getProjectId());
			}
		}
		// 组装project信息
		if (projectIds.size() > 0) {
			List<Project> projects = projectMapper.selectList(new LambdaQueryWrapper<Project>().in(Project::getId, projectIds));
			List<Long> userIds = new ArrayList<>();
			projects.forEach(project -> {
				if (project.getOwner() > 0) {
					userIds.add(project.getOwner());
				}
			});
			UserListByIdsReq req = new UserListByIdsReq();
			req.setOrgId(orgId);
			req.setIds(userIds);
			List<UserInfoResp> users = userCenterApi.getAllUserListByIds(req).getData();
			HashMap<Long, MemberResp> userMap = new HashMap<>();
			users.forEach(user -> {
				userMap.put(user.getId(), new MemberResp(user.getId(), user.getName(), user.getAvatar(), AppConsts.MEMBER_USER_TYPE, user.getStatus(), user.getIsDelete()));
			});

			projects.forEach(project -> {
				ProjectResp projectResp = new ProjectResp();
				projectResp.setId(project.getId());
				projectResp.setOrgId(project.getOrgId());
				projectResp.setAppId(project.getAppId());
				projectResp.setName(project.getName());
				projectResp.setProjectTypeId(project.getProjectTypeId());
				projectResp.setResourceId(project.getResourceId());
				projectResp.setIsFilling(project.getIsFilling());
				projectResp.setRemark(project.getRemark());
				projectResp.setStatus(project.getStatus());
				projectResp.setCreator(project.getCreator());
				projectResp.setCreateTime(project.getCreateTime());
				projectResp.setUpdator(project.getUpdator());
				projectResp.setUpdateTime(project.getUpdateTime());
				projectResp.setIsDelete(project.getIsDelete());
				if (project.getOwner() > 0 && userMap.containsKey(project.getOwner())) {
					projectResp.setOwner(userMap.get(project.getOwner()));
				}
				resp.getProjectListResp().add(projectResp);
			});
		}
        return resp;
	}

	public Set<Long> getUserVisibleAppIds(UserPermissionVO userPermissionVO){
		Set<Long> hasViewAppIds = new HashSet<>();
		if (userPermissionVO.getManageApps() != null) {
			hasViewAppIds.addAll(userPermissionVO.getManageApps());
		}
		log.info("getUserVisibleAppIds userId:{}, appIds:{}", userPermissionVO.getUserId(), hasViewAppIds);
		LambdaQueryWrapper<AppRelation> appRelationsQuery = new LambdaQueryWrapper<>();
		appRelationsQuery.eq(AppRelation::getDelFlag, CommonConsts.FALSE);
		appRelationsQuery.eq(AppRelation::getOrgId, userPermissionVO.getOrgId());
		appRelationsQuery.and(wrapper -> {
			wrapper.or().eq(AppRelation::getType, AppRelationType.USER.getCode()).eq(AppRelation::getRelationId, userPermissionVO.getUserId());
			wrapper.or().eq(AppRelation::getType, AppRelationType.DEPT.getCode()).eq(AppRelation::getRelationId, 0);
			if (CollectionUtils.isNotEmpty(userPermissionVO.getRefDeptIds())){
				wrapper.or().eq(AppRelation::getType, AppRelationType.DEPT.getCode()).in(AppRelation::getRelationId, userPermissionVO.getRefDeptIds());
			}
			if (CollectionUtils.isNotEmpty(userPermissionVO.getRefRoleIds())){
				wrapper.or().eq(AppRelation::getType, AppRelationType.ROLE.getCode()).in(AppRelation::getRelationId, userPermissionVO.getRefRoleIds());
			}
			return wrapper;
		});
		List<AppRelation> appRelations = appRelationMapper.selectList(appRelationsQuery);
		if (CollectionUtils.isNotEmpty(appRelations)){
			hasViewAppIds.addAll(appRelations.stream().map(AppRelation::getAppId).collect(Collectors.toList()));
		}
		log.info("getUserVisibleAppIds2 userId:{}, appIds:{}", userPermissionVO.getUserId(), hasViewAppIds);
		//获取项目成员关联的应用
		LambdaQueryWrapper<ProjectRelation> projectRelationLambdaQueryWrapper = new LambdaQueryWrapper<>();
		projectRelationLambdaQueryWrapper.eq(ProjectRelation::getIsDelete, CommonConsts.FALSE);
		projectRelationLambdaQueryWrapper.eq(ProjectRelation::getOrgId, userPermissionVO.getOrgId());
		projectRelationLambdaQueryWrapper.and(wrapper -> {
			wrapper.or().eq(ProjectRelation::getRelationType, ProjectRelationType.OWNER.getCode()).eq(ProjectRelation::getRelationId, userPermissionVO.getUserId());
			wrapper.or().eq(ProjectRelation::getRelationType, ProjectRelationType.PARTICIPANT.getCode()).eq(ProjectRelation::getRelationId, userPermissionVO.getUserId());
//			wrapper.or().eq(ProjectRelation::getRelationType, ProjectRelationType.FOLLOWER.getCode()).eq(ProjectRelation::getRelationId, userPermissionVO.getUserId());
			wrapper.or().eq(ProjectRelation::getRelationType, ProjectRelationType.DEPT.getCode()).eq(ProjectRelation::getRelationId, 0);
			if (CollectionUtils.isNotEmpty(userPermissionVO.getRefDeptIds())) {
				wrapper.or().eq(ProjectRelation::getRelationType, ProjectRelationType.DEPT.getCode()).in(ProjectRelation::getRelationId, userPermissionVO.getRefDeptIds());
			}
			return wrapper;
		});
		List<ProjectRelation> projectRelations = projectRelationMapper.selectList(projectRelationLambdaQueryWrapper);
		if (CollectionUtils.isNotEmpty(projectRelations)) {
			List<Long> allRelateProjectIds = new ArrayList<>();
			for (ProjectRelation projectRelation: projectRelations) {
				allRelateProjectIds.add(projectRelation.getProjectId());
			}

			LambdaQueryWrapper<App> appLambdaQueryWrapper = new LambdaQueryWrapper<>();
			appLambdaQueryWrapper.eq(App::getDelFlag, CommonConsts.FALSE);
			appLambdaQueryWrapper.eq(App::getOrgId, userPermissionVO.getOrgId());
			appLambdaQueryWrapper.in(App::getProjectId, allRelateProjectIds);
			appLambdaQueryWrapper.ne(App::getType, AppType.MIRROR.getCode());
			List<App> appList = appMapper.selectList(appLambdaQueryWrapper);
			if (CollectionUtils.isNotEmpty(appList)) {
				hasViewAppIds.addAll(appList.stream().map(App::getId).collect(Collectors.toList()));
			}
		}
		log.info("getUserVisibleAppIds3 userId:{}, appIds:{}", userPermissionVO.getUserId(), hasViewAppIds);
		return hasViewAppIds;
	}

	private Set<Long> getTheViewAppIds(List<App> apps, Set<Long> hasViewAppIds) {
//		Set<Long> hasViewAppIds = getUserVisibleAppIds(userPermissionVO);
//		Map<Long, List<Long>> parentIdMap = new HashMap<>();	// parent -> list of child
		Map<Long, Long> childIdMap = new HashMap<>();	// child -> parent
		for (App app: apps){
//			if (app.getParentId() > 0){
//				List<Long> ids = parentIdMap.computeIfAbsent(app.getParentId(), k -> new ArrayList<>());
//				ids.add(app.getId());
//			}
			childIdMap.put(app.getId(), app.getParentId());
		}
//		Queue<Long> queue = new LinkedBlockingQueue<>(hasViewAppIds);
//		Long parentAppId = null;
//		while((parentAppId = queue.poll()) != null){
//			List<Long> ids = parentIdMap.get(parentAppId);
//			if (CollectionUtils.isNotEmpty(ids)){
//				ids.removeIf(hasViewAppIds::contains);
//				queue.addAll(ids);
//				hasViewAppIds.addAll(ids);
//			}
//		}
//		// 我创建的
////		hasViewAppIds.addAll(apps.stream().filter(a -> Objects.equals(a.getCreator(), userPermissionVO.getUserId())).map(App::getId).collect(Collectors.toList()));
//		// 所有父任务id
		Queue<Long> queue = new LinkedBlockingQueue<>(hasViewAppIds);
		Long childAppId = null;
		while((childAppId = queue.poll()) != null){
			Long parentId = childIdMap.get(childAppId);
			if (Objects.nonNull(parentId) && parentId > 0 && ! hasViewAppIds.contains(parentId)){
				queue.add(parentId);
				hasViewAppIds.add(parentId);
			}
		}
		return hasViewAppIds;
	}

	public AppResp get(Long appId, Long orgId, Long userId) {
		App app = appMapper.get(orgId, appId);
		if(app == null) {
			throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " org:" + orgId + " app:" + appId);
		}

//		OrgUserPermissionContext orgUserPermissionContext = new OrgUserPermissionContext(permissionUtil.getUserPermission(orgId, userId).getData());
//		if(! orgUserPermissionContext.hasManageAppPackagePermission(app.getPkgId())){
//			throw new BusinessException(ResultCode.NO_READ_APP_PERMISSION.getCode(), ResultCode.NO_READ_APP_PERMISSION.getMessage());
//		}
		AppResp appResp = ConvertUtil.convert(app, AppResp.class);
		if(AppStatus.DISABLE.getCode().equals(appResp.getStatus())){// 禁用
			appResp.setHidden(YesOrNo.YES.getCode());
		}
		if(app.getProjectId() > 0) {
			Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getId, app.getProjectId()));
			if(project != null) {
				appResp.setProjectTypeId(project.getProjectTypeId());
			}
		}
		return appResp;
	}

	public AppResp get(Long appId) {
		App app = appMapper.get(appId);
		if(app == null) {
			throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " app:" + appId);
		}
		return ConvertUtil.convert(app, AppResp.class);
	}

	@Transactional(rollbackFor = Exception.class)
	public AppResp add(AppAddReq req, Long orgId, Long userId) {
		if (req.getParentId() != null && req.getParentId() > 0){
			AppAuthorityResp appAuthorityResp = permissionService.appAuth(orgId, req.getParentId(), userId);
			if (! appAuthorityResp.hasAppRootPermission()){
				throw new BusinessException(ResultCode.NO_ADD_APP_PERMISSION);
			}
		}
		if (req.getMirrorAppId() != null && req.getMirrorAppId() > 0){
			AppAuthorityResp appAuthorityResp = permissionService.appAuth(orgId, req.getMirrorAppId(), userId);
			if (! appAuthorityResp.hasAppRootPermission()){
				throw new BusinessException(ResultCode.NO_ADD_APP_PERMISSION);
			}
		}

		if (StringUtils.isBlank(req.getName())){
			throw new BusinessException(ResultCode.APP_NAME_CANNOT_EMPTY);
		}

		App app = new App();
		app.setPkgId(req.getPkgId());
		app.setName(req.getName());
		if(AppType.formatOrNull(req.getType()) == null){
			throw new BusinessException(ResultCode.UNSUPPORT_APP_TYPE.getCode(), ResultCode.UNSUPPORT_APP_TYPE.getMessage());
		}
		app.setType(req.getType());
		app.setIcon(req.getIcon());
		app.setLinkUrl(req.getLinkUrl());
		app.setOrgId(orgId);
		app.setCreator(userId);
		app.setUpdator(userId);
		app.setExtendsId(req.getExtendsId());
		app.setWorkflowFlag(req.getWorkflowFlag());
		app.setSort(groupService.getMinSort(orgId) - 1);
		app.setDelFlag(CommonConsts.NO_DELETE);
//		app.setAuthType(req.getAuthType());
		app.setAuthType(2); // 默认都不继承
        app.setParentId(req.getParentId());
        app.setMirrorAppId(req.getMirrorAppId());
        app.setMirrorViewId(req.getMirrorViewId());
        app.setMirrorTypeId(req.getMirrorTypeId());
		app.setMirrorTableId(req.getMirrorTypeId());
		if (Objects.equals(req.getType(), AppType.MIRROR.getCode())){
			App mirrorApp = appMapper.get(req.getMirrorAppId());
			if (mirrorApp == null){
				throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " app:" + req.getMirrorAppId());
			}
			if (! Objects.equals(mirrorApp.getType(), AppType.SUMMARY.getCode()) &&
					! Objects.equals(mirrorApp.getType(), AppType.FORM.getCode()) &&
					! Objects.equals(mirrorApp.getType(), AppType.PROJECT.getCode())){
				throw new BusinessException(ResultCode.APP_MIRROR_INVALID_APP_TYPE);
			}
			app.setProjectId(mirrorApp.getProjectId());
		}
		boolean suc = appMapper.insert(app) > 0;
		if(suc) {
            // 添加创建人为成员
            if (app.getCreator() > 0) {
                AppRelation appRelation = new AppRelation();
                appRelation.setAppId(app.getId());
                appRelation.setRelationId(app.getCreator());
                appRelation.setType(AppRelationType.USER.getCode());
                appRelation.setOrgId(app.getOrgId());
                appRelation.setCreator(app.getCreator());
                appRelation.setUpdator(app.getUpdator());
                appRelationMapper.insert(appRelation);
            }

			InitAppPermissionReq initPerReq = new InitAppPermissionReq();
			AppResp appResp = ConvertUtil.convert(app, AppResp.class);
			if(AppType.DASHBOARD.getCode().equals(req.getType())) {
				DashboardCreate dashboardCreate = new DashboardCreate();
				dashboardCreate.setOrgId(orgId);
				dashboardCreate.setAppId(app.getId());
				dashboardCreate.setName(app.getName());
				dashboardCreate.setType((short) 1);
				dashboardCreate.setWidgetFlag((short)0);
				DashboardResp resp = RpcUtil.accept(dashboardProvider.createDashboard(dashboardCreate));
				appResp.setDashboardResp(resp);
				initPerReq.setDefaultPermissionGroupType(DefaultAppPermissionGroupType.POLARIS_PROJECT.getCode());
				log.info("创建仪表盘成功： {}", JSON.toJSONString(resp));
			}else if(AppType.FORM.getCode().equals(req.getType())
					|| AppType.FOLDER.getCode().equals(req.getType())
					|| AppType.PACKAGE.getCode().equals(req.getType())
					|| AppType.SUMMARY.getCode().equals(req.getType())
					|| AppType.PROJECT.getCode().equals(req.getType())) {

				if(AppType.PROJECT.getCode().equals(req.getType())){
					initPerReq.setDefaultPermissionGroupType(DefaultAppPermissionGroupType.POLARIS_PROJECT.getCode());
				}else{
					initPerReq.setDefaultPermissionGroupType(DefaultAppPermissionGroupType.FORM.getCode());
				}
				log.info("创建应用表单成功");
			}else if (AppType.MIRROR.getCode().equals(req.getType())){
				initPerReq.setDefaultPermissionGroupType(DefaultAppPermissionGroupType.POLARIS_PROJECT.getCode());
			}else {
				throw new BusinessException(ResultCode.UNSUPPORT_APP_TYPE);
			}
			initPerReq.setOrgId(orgId);
			initPerReq.setAppId(app.getId());
			initPerReq.setUserId(userId);
			initPerReq.setAppType(req.getType());
			boolean b = permissionUtil.initDefaultPermissionGroup(initPerReq).getData();
			if(! b){
				throw new BusinessException(ResultCode.INIT_FORM_PERMISSION_GROUP_FAIL.getCode(), ResultCode.INIT_FORM_PERMISSION_GROUP_FAIL.getMessage());
			}

			// 设置极星项目创建人，默认是管理员角色
			List<AppPerGroupListItem> perGroups = permissionUtil.getAppPermissionGroupList(orgId, app.getId());
			log.info("[createApp] 获取权限组: {}", perGroups);
            for (AppPerGroupListItem perGroup : perGroups) {
                if (perGroup.getLangCode().equals(AppPerDefaultGroupLangCode.OWNER.getCode()) ||
                        perGroup.getLangCode().equals(AppPerDefaultGroupLangCode.FORM_ADMINISTRATOR.getCode()) ||
                        perGroup.getLangCode().equals(AppPerDefaultGroupLangCode.DASHBOARD_ADMINISTRATOR.getCode())) {
                    List<String> memberIds = new ArrayList<>();
                    memberIds.add(AppConsts.MEMBER_USER_TYPE + userId.toString());
                    AddAppMembersReq addAppMembersReq = new AddAppMembersReq();
                    addAppMembersReq.setOrgId(orgId);
                    addAppMembersReq.setAppId(app.getId());
                    addAppMembersReq.setMemberIds(memberIds);
                    addAppMembersReq.setPerGroupId(perGroup.getId());
                    log.info("[createApp] 添加创建人到权限组: {}", addAppMembersReq);
                    suc = permissionUtil.addAppMembers(addAppMembersReq);
                    if (!suc) {
                        throw new BusinessException(ResultCode.INIT_APP_CREATOR_PERMISSION_GROUP_FAIL);
                    }
                    break;
                }
            }

			// 目前就这三个去创建表，其他都不创建了
			if (AppType.SUMMARY.getCode().equals(req.getType())){
				goTableProvider.createSummery(new CreateSummeryTableRequest(app.getId(),req.getFormFields()),orgId.toString(), userId.toString());
			} else if (AppType.FORM.getCode().equals(req.getType())){
				goTableProvider.create(new CreateTableRequest(app.getId(),"项目表单",null,null,true,true, req.getType(), req.getFormFields()),orgId.toString(), userId.toString());
			} else if (AppType.PROJECT.getCode().equals(req.getType())){
				// 这里有个问题，如果后续需要创建3个表的话，可能需要极星那边去调用了，目前先这样。
				goTableProvider.create(new CreateTableRequest(app.getId(),"任务",req.getBaseFields(),null,false,true,req.getType(),null),orgId.toString(), userId.toString());
			}

			return appResp;
		}
		throw new BusinessException(ResultCode.APP_ADD_FAIL.getCode(), ResultCode.APP_ADD_FAIL.getMessage());
	}

	public AppResp update(Long appId, AppUpdateReq req, Long orgId, Long userId) {
		App oldApp = appMapper.get(orgId, appId);
		if(oldApp == null) {
			throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " org:" + orgId + " app:" + appId);
		}
		AppAuthorityResp appAuthorityResp = permissionService.appAuth(orgId, appId, userId);
		if(! appAuthorityResp.hasAppRootPermission()){
			throw new BusinessException(ResultCode.NO_MODIFY_APP_PERMISSION.getCode(), ResultCode.NO_MODIFY_APP_PERMISSION.getMessage());
		}
		App app = new App();
		app.setId(appId);
		if(! StringUtils.isBlank(req.getName())){
			app.setName(req.getName());
		}
//		if(AppType.formatOrNull(req.getType()) != null){
//			app.setType(req.getType());
//		}
		if(Objects.nonNull(req.getIcon())){
			app.setIcon(req.getIcon());
		}
		if(! StringUtils.isBlank(req.getRemark())){
			app.setRemark(req.getRemark());
		}
		if(req.getExtendsId() != null){
			app.setExtendsId(req.getExtendsId());
		}
		if (StringUtils.isNotBlank(req.getLinkUrl())){
			app.setLinkUrl(req.getLinkUrl());
		}
		if (req.getWorkflowFlag() != null){
			app.setWorkflowFlag(req.getWorkflowFlag());
		}
		app.setUpdator(userId);
		appMapper.updateById(app);

		// 事件上报
		ThreadPools.POOLS.execute(() -> {
			App newApp = appMapper.get(appId);
			if (newApp == null) {
				throw new BusinessException(ResultCode.APP_NOT_EXIST);
			}
			AppEvent appEvent = new AppEvent();
			appEvent.setOrgId(orgId);
			appEvent.setAppId(appId);
			appEvent.setProjectId(newApp.getProjectId());
			appEvent.setUserId(userId);
			if (newApp.getProjectId() > 0) {
				Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getId, newApp.getProjectId()).last("limit 1"));
				if (project == null) {
					throw new BusinessException(ResultCode.PROJECT_NOT_EXIST);
				}
				appEvent.setProject(project);
			}
			List<AppRelation> appRelations = appRelationMapper.selectList(new LambdaQueryWrapper<AppRelation>()
					.eq(AppRelation::getOrgId, orgId)
					.eq(AppRelation::getDelFlag, CommonConsts.FALSE)
                    .eq(AppRelation::getAppId, appId)
					.eq(AppRelation::getType, AppRelationType.STAR.getCode())
					.eq(AppRelation::getRelationId, userId)
			);
			newApp.setHasStared(appRelations.size() > 0);
            appEvent.setApp(newApp);
			projectApi.reportAppEvent(AppConsts.EventTypeAppRefresh, "", appEvent);
		});
		return ConvertUtil.convert(app, AppResp.class);
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean delete(Long id, Long orgId, Long userId) {
		App app = appMapper.get(orgId, id);
		if (app == null) {
			throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " org:" + orgId + " app:" + id);
		}
		AppAuthorityResp appAuthorityResp = permissionService.appAuth(orgId, app.getId(), userId);
		if(! appAuthorityResp.hasAppRootPermission()){
			throw new BusinessException(ResultCode.NO_DELETE_APP_PERMISSION.getCode(), ResultCode.NO_DELETE_APP_PERMISSION.getMessage());
		}

		List<Long> appIds = new ArrayList<>();
		appIds.add(id);

		if(AppType.SUMMARY.getCode().equals(app.getType())){
			List<App> apps = this.baseMapper.getAppsByExtendsId(app.getId());
			if(!CollectionUtils.isEmpty(apps)){
				appIds.addAll(apps.stream().map(App :: getId).collect(Collectors.toList()));
			}
		}

		int affects = appMapper.update(null, new LambdaUpdateWrapper<App>()
				.set(App :: getDelFlag, CommonConsts.TRUE)
				.set(App :: getUpdator, userId)
				.eq(App :: getDelFlag, CommonConsts.FALSE)
				.in(App :: getId, appIds));
		if (affects == 0) {
			throw new BusinessException(ResultCode.APP_MODIFY_FAIL.getCode(), ResultCode.APP_MODIFY_FAIL.getMessage());
		}

		// 删除widgets
		dashboardProvider.deleteAppWidgets(Collections.singletonList(id));
		// 删除视图
		appViewMapper.update(null, new LambdaUpdateWrapper<AppView>().eq(AppView::getAppId, id).eq(AppView::getDelFlag, CommonConsts.FALSE).set(AppView::getDelFlag, CommonConsts.TRUE));

		// 事件上报
		ThreadPools.POOLS.execute(() -> {
			AppEvent appEvent = new AppEvent();
			appEvent.setOrgId(orgId);
			appEvent.setAppId(app.getId());
			appEvent.setProjectId(app.getProjectId());
			appEvent.setUserId(userId);
			projectApi.reportAppEvent(AppConsts.EventTypeAppDeleted, "", appEvent);
		});

		// 暂时不删其实也没事
		// formProvider.deleteForm(appIds, userId);
		return true;
	}

	public void batchDelete(Long orgId, List<Long> pkgIds, Long userId) {
		List<App> listApps = appMapper.getByPkgIds(pkgIds);
		List<Long> appIds = new ArrayList<>();
		appIds = listApps.stream().map(p -> p.getId()).collect(Collectors.toList());
		LambdaUpdateWrapper<App> updateWrapper = new LambdaUpdateWrapper<>();
		updateWrapper.set(App :: getDelFlag, CommonConsts.DELETED)
				.set(App :: getUpdator, userId)
				.eq(App :: getOrgId, orgId)
				.in(App :: getPkgId, pkgIds)
				.eq(App :: getDelFlag, CommonConsts.NO_DELETE);
		appMapper.update(null, updateWrapper);
		if(! CollectionUtils.isEmpty(appIds)){
			// 目前极星没用到，直接先注释
//			formProvider.deleteForm(appIds, userId);
		}
	}

	// 暂不用
	public void publish(Long id, Long orgId, Long userId) {
		App app = appMapper.get(orgId, id);
		if(app == null) {
			throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " org:" + orgId + " app:" + id);
		}
		List<AppVersion> appVersions = appVersionMapper.selectList(new LambdaQueryWrapper<AppVersion>()
									.eq(AppVersion :: getAppId, id)
									.eq(AppVersion :: getStatus, AppVersionStatus.DRAFT.getStatus())
									.eq(AppVersion :: getDelFlag, CommonConsts.NO_DELETE));
		//如果没有任何草稿，直接返回
		if(CollectionUtils.isEmpty(appVersions)) {
			return;
		}

		for(AppVersion appVersion: appVersions) {
			if(AppVersionType.FORM.getType().equals(appVersion.getType())) {
				publicForm(app, appVersion, orgId, userId);
			}else if(AppVersionType.WORKFLOW.getType().equals(appVersion.getType())) {
				//TODO
			}else if(AppVersionType.APP_INFO.getType().equals(appVersion.getType())) {
				publicAppInfo(app, appVersion);
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public Result<?> excelSave(AppFormExcelSaveReq appFormExcelSaveReq, Long orgId, Long userId) {
		OrgUserPermissionContext orgUserPermissionContext = new OrgUserPermissionContext(permissionUtil.getUserPermission(orgId, userId).getData());
		if(! orgUserPermissionContext.hasManageAppPackagePermission(appFormExcelSaveReq.getPkgId())){
			throw new BusinessException(ResultCode.NO_ADD_APP_PERMISSION.getCode(), ResultCode.NO_ADD_APP_PERMISSION.getMessage());
		}
		// 1. 创建应用
		AppAddReq appAddReq = new AppAddReq();
		appAddReq.setName(appFormExcelSaveReq.getName());
		appAddReq.setPkgId(appFormExcelSaveReq.getPkgId());
		appAddReq.setType(AppType.FORM.getCode());
		AppResp add = innerAdd(appAddReq, orgId, userId);
		List<Map<String, Object>> configList = appFormExcelSaveReq.getConfig();
		Map<String, Object> defaultMap = (Map<String, Object>) JSONObject.parse(DEFAULT_HEADER_CONFIG);
		List<Map<String, Object>> realConfigList = new ArrayList<>();
		configList.forEach(configMap -> {
			Map<String, Object> mergeMap = new LinkedHashMap<>(defaultMap);
			configMap.forEach((s, o) -> mergeMap.merge(s, o , (v1, v2) -> v2));
			mergeMap.put("keyName", FieldKeyNameUtils.generateKeyName());
			realConfigList.add(mergeMap);
		});

		List<FieldParam> listFields = new ArrayList<>();
		for (Map<String, Object> map : realConfigList) {
			FieldParam fieldParam = new FieldParam();
			fieldParam.setName((String) map.get("keyName"));
			fieldParam.setLabel((String) map.get("lable"));
			Field f = new Field();
			if (FieldTypeEnums.formatOrNull((Integer) map.get("type")) != null) {
				f.setType(FieldTypeEnums.formatOrNull((Integer) map.get("type")).getFormFieldType());
				f.setDataType(FieldTypeEnums.formatOrNull((Integer) map.get("type")).getType());
			}
			fieldParam.setField(f);
			listFields.add(fieldParam);
		}
		//appFormExcelSaveReq.setConfig(realConfigList);

		goTableProvider.createSummery(new CreateSummeryTableRequest(add.getId(),listFields),orgId.toString(),userId.toString());
		InitAppPermissionReq initPerReq = new InitAppPermissionReq();
		initPerReq.setOrgId(orgId);
		initPerReq.setAppId(add.getId());
		initPerReq.setUserId(userId);
		initPerReq.setAppType(add.getType());
		boolean b = permissionUtil.initDefaultPermissionGroup(initPerReq).getData();
		if(! b){
			throw new BusinessException(ResultCode.INIT_FORM_PERMISSION_GROUP_FAIL.getCode(), ResultCode.INIT_FORM_PERMISSION_GROUP_FAIL.getMessage());
		}
		return Result.ok(add);
	}

	private List<String> getFieldNames(FieldParam fieldParam, List<String> fieldNames){
		fieldNames.add(fieldParam.getName());
		return fieldNames;
	}

	public Result<?> formSave(com.polaris.lesscode.app.req.AppFormSaveReq appFormDesignSaveReq, Long orgId, Long userId) {
		return null;
//		App app = appMapper.get(orgId, appFormDesignSaveReq.getAppId());
//		if(app == null){
//			throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " org:" + orgId + " app:" + appFormDesignSaveReq.getAppId());
//		}
//
//		AppAuthorityResp appAuthorityResp = permissionService.appAuth(orgId, app.getId(), userId);
//		if(! appAuthorityResp.hasAppOptAuth(OperateAuthCode.HAS_UPDATE.getCode())){
//			throw new BusinessException(ResultCode.NO_MODIFY_APP_PERMISSION.getCode(), ResultCode.NO_MODIFY_APP_PERMISSION.getMessage());
//		}
//		app.setName(appFormDesignSaveReq.getName());
//		appMapper.updateById(app);
//		List<FieldParam> configList = appFormDesignSaveReq.getConfig();
//		if(! CollectionUtils.isEmpty(configList)){
//			List<String> fieldNames = new ArrayList<>();
//			for (FieldParam fieldParam : configList) {
//				fieldNames = getFieldNames(fieldParam, fieldNames);
//				if(! CollectionUtils.isEmpty(fieldParam.getFields())){
//					for (FieldParam fieldParam2 : fieldParam.getFields()) {
//						fieldNames = getFieldNames(fieldParam2, fieldNames);
//					}
//				}
//			}
//			//TODO 加强校验
//			if(! CollectionUtils.isEmpty(fieldNames)){
//				long count = fieldNames.stream().map(String::trim).distinct().count();
//				if(count < fieldNames.size()){
//					throw new BusinessException(ResultCode.APP_FORM_FIELD_REPEAT.getCode(), ResultCode.APP_FORM_FIELD_REPEAT.getMessage());
//				}
//			}
//		}
//
//		FormTemplate template = new FormTemplate();
//		FormJson formJson = new FormJson();
//		formJson.setMode(template.getMode());
//		formJson.setFields(configList);
//		formJson.setBaseFields(appFormDesignSaveReq.getBaseFields());
//		formJson.setFieldOrders(appFormDesignSaveReq.getFieldOrders());
//
//		String jsonForm = JSONObject.toJSONString(formJson);
//
//		if (!permissionUtil.updateFormFieldConfig(app.getOrgId(), app.getId(), jsonForm)) {
//			throw new BusinessException(ResultCode.APP_FORM_FIELD_UPDATE_FAIL);
//		}
//
//		AppFormSaveReq appFormSaveReq = new AppFormSaveReq();
//		appFormSaveReq.setConfig(jsonForm);
//		appFormSaveReq.setAppId(appFormDesignSaveReq.getAppId());
//		appFormSaveReq.setDrafted(false);
//		appFormSaveReq.setOrgId(orgId);
//		appFormSaveReq.setUserId(userId);
//
//		goTableProvider.createSummery(new CreateSummeryTableRequest(appFormDesignSaveReq.getAppId(), formJson.getFields()),orgId.toString(),userId.toString());
//		return Result.ok(appFormSaveReq);
	}

	private void publicAppInfo(App app, AppVersion appVersion) {
		String config = appVersion.getConfig();
		App draft = JSON.parseObject(config, App.class);

		App update = new App();
		update.setId(app.getId());
		update.setName(draft.getName());
		update.setIcon(draft.getIcon());
		appMapper.updateById(update);
	}

	private void publicForm(App app, AppVersion appVersion, Long orgId, Long userId) {
//		AppFormSaveReq appFormSaveReq = new AppFormSaveReq();
//		appFormSaveReq.setConfig(appVersion.getConfig());
//		appFormSaveReq.setAppId(app.getId());
//		appFormSaveReq.setDrafted(false);
//		appFormSaveReq.setOrgId(orgId);
//		appFormSaveReq.setUserId(userId);
		// 不知道有啥用，先去掉吧
//		RpcUtil.accept(goTableProvider.createSummery(new CreateSummeryTableRequest((app.getId(), appVersion.getConfig()),orgId.toString(),userId.toString()));
	}

	private AppResp innerAdd(AppAddReq req, Long orgId, Long userId) {
		App app = new App();
		app.setPkgId(req.getPkgId() == null ? 0 :req.getPkgId());
		app.setName(req.getName());
		app.setType(req.getType());
		app.setIcon(req.getIcon());
		app.setOrgId(orgId);
		app.setCreator(userId);
		app.setUpdator(userId);
		app.setSort(groupService.getMinSort(orgId) + 1);

		boolean suc = appMapper.insert(app) > 0;
		if(suc) {
			return ConvertUtil.convert(app, AppResp.class);
		}
		throw new BusinessException(ResultCode.APP_ADD_FAIL.getCode(), ResultCode.APP_ADD_FAIL.getMessage());
	}

	@Transactional(rollbackFor = Exception.class)
	public MoveAppListResp moveApp(Long orgId, Long userId, MoveAppReq req) {
		MoveAppListResp resp = new MoveAppListResp();
		resp.setMoveAppRespList(new ArrayList<>());
		AppAuthorityResp appAuthorityResp = permissionService.appAuth(orgId, req.getId(), userId);
		if (! appAuthorityResp.hasAppOptAuth(OperateAuthCode.HAS_UPDATE.getCode())) {
			throw new BusinessException(ResultCode.NO_APP_MOVE_PERMISSION.getCode(), ResultCode.NO_APP_MOVE_PERMISSION.getMessage());
		}
		App updateEntity = new App();
		if (req.getBeforeId() != null || req.getAfterId() != null) {
			boolean isBeforeReference = req.getBeforeId() != null;
			AfterMoveBo afterMove = groupService.updateReferenceSort(
					orgId,
					isBeforeReference ? req.getBeforeId() : req.getAfterId(),
					req.getType(),
					isBeforeReference ? ReferenceType.BEFORE.getType() : ReferenceType.AFTER.getType());
			if (afterMove != null) {
				updateEntity.setPkgId(afterMove.getParentId());
				updateEntity.setParentId(afterMove.getParentId());
				updateEntity.setSort(afterMove.getSort());
			}
		} else {// 无参照物
			AfterMoveBo afterMove = groupService.updateNoReferenceSort(req.getParentId());
			if (afterMove != null) {
				updateEntity.setPkgId(afterMove.getParentId());
				updateEntity.setParentId(afterMove.getParentId());
				updateEntity.setSort(afterMove.getSort());
			}
		}

		if (StringUtils.isNotBlank(req.getName())) {
			updateEntity.setName(req.getName());
		}

		updateEntity.setId(req.getId());
		this.baseMapper.updateById(updateEntity);

		List<App> apps = appMapper.selectList(new LambdaQueryWrapper<App>()
				.eq(App::getOrgId, orgId)
				.in(App::getParentId, updateEntity.getParentId())
				.eq(App::getDelFlag, 2)
		);
		apps.forEach(app -> {
			MoveAppResp moveAppResp = new MoveAppResp();
			moveAppResp.setAppId(app.getId());
			moveAppResp.setSort(app.getSort());
			moveAppResp.setType(app.getType());
			moveAppResp.setParentId(app.getParentId());

			resp.getMoveAppRespList().add(moveAppResp);
		});

		return resp;
	}

	/**
	 * 切换应用的authType
	 *
	 * @Date 2021/6/1 16:06
	 **/
	public void switchAuthType(Long orgId, Long userId, Long appId, AppSwitchAuthTypeReq req){
		App app = appMapper.get(orgId, appId);
		if(app == null){
			throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " org:" + orgId + " app:" + appId);
		}
		if (! Objects.equals(req.getAuthType(), 1) && ! Objects.equals(req.getAuthType(), 2)){
			throw new BusinessException(ResultCode.APP_AUTH_TYPE_INVALID);
		}
		if(Objects.equals(app.getAuthType(), req.getAuthType())){
			return;
		}

		App updated = new App();
		updated.setId(app.getId());
		updated.setUpdator(userId);
		updated.setAuthType(req.getAuthType());
//		if (Objects.equals(app.getAuthTypeSwitchFlag(), 2) && Objects.equals(req.getAuthType(), 2)){
//			switchAuthTypeToCustom(orgId, userId, app);
//			updated.setAuthTypeSwitchFlag(1);
//		}
		appMapper.updateById(updated);
	}

	/**
	 * 从继承切换到自定义，直接删掉自定义权限以及成员
	 *
	 * @Date 2021/6/1 16:22
	 **/
//	private void switchAuthTypeToCustom(Long orgId, Long userId, App app){
//		App sourceApp = appRelationService.getExtendsAuthApp(app);
//		if (! Objects.equals(sourceApp.getId(), app.getId())){
//			CopyAppPermissionGroupReq req = new CopyAppPermissionGroupReq();
//			req.setSourceAppId(sourceApp.getId());
//			req.setTargetAppId(app.getId());
//			permissionProvider.copyPermissionGroup(req);
//			appRelationService.copyAppRelationMember(sourceApp.getId(), app.getId());
//		}
//	}


	/**
	 * 做数据同步相关的事情
	 * 当前：老的pkg数据同步到app中
	 *
	 * @Author Nico
	 * @Date 2021/5/25 13:50
	 **/
	public void dataSchedule(){
		List<AppPackage> packages = appPackageMapper.selectList(new LambdaQueryWrapper<AppPackage>().eq(AppPackage::getDelFlag, CommonConsts.FALSE));
		int index = 1;
		for (AppPackage pkg: packages){		// 老应用包的权限组不用处理
			App app = new App();
			app.setId(pkg.getId());
			app.setOrgId(pkg.getOrgId());
			app.setDelFlag(2);
			app.setSort(pkg.getSort().longValue());
			app.setName(pkg.getName());
			app.setParentId(pkg.getParentId());
			app.setType(AppType.FOLDER.getCode());
			app.setCreator(pkg.getCreator());
			app.setCreateTime(pkg.getCreateTime());
			app.setUpdator(pkg.getUpdator());
			app.setUpdateTime(pkg.getUpdateTime());
			try {
				appMapper.insert(app);
			}catch (Exception e){
			}finally {
				System.out.println("pkg schedule: " + (index ++) + "/" + packages.size());
			}
		}
		index = 1;
		List<App> apps = appMapper.selectList(new LambdaQueryWrapper<App>().eq(App::getDelFlag, CommonConsts.FALSE).in(App::getType, Arrays.asList(AppType.FORM.getCode(), AppType.SUMMARY.getCode())));
		for (App app: apps){
			App updated = new App();
			updated.setId(app.getId());
			updated.setParentId(app.getPkgId());
			updated.setAuthType(2);	//老应用的认证类型都为自定义
			try {
				appMapper.updateById(updated);
			}catch (Exception e){
			}finally {
				System.out.println("app schedule: " + (index ++) + "/" + apps.size());
			}
		}
	}

}
