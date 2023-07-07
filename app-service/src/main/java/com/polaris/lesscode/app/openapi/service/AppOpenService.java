package com.polaris.lesscode.app.openapi.service;

//import com.alibaba.fastjson.JSON;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.polaris.lesscode.app.entity.App;
//import com.polaris.lesscode.app.enums.AppStatus;
//import com.polaris.lesscode.app.internal.enums.AppType;
//import com.polaris.lesscode.app.enums.YesOrNo;
//import com.polaris.lesscode.app.mapper.AppMapper;
//import com.polaris.lesscode.app.openapi.req.AppOpenAddReq;
//import com.polaris.lesscode.app.openapi.req.AppOpenUpdateReq;
//import com.polaris.lesscode.app.resp.AppResp;
//import com.polaris.lesscode.app.service.AppPackageService;
//import com.polaris.lesscode.app.utils.PermissionUtil;
//import com.polaris.lesscode.app.vo.ResultCode;
//import com.polaris.lesscode.consts.CommonConsts;
//import com.polaris.lesscode.dashboard.internal.req.DashboardCreate;
//import com.polaris.lesscode.dashboard.internal.resp.DashboardResp;
//import com.polaris.lesscode.exception.BusinessException;
//import com.polaris.lesscode.form.internal.api.AppFormApi;
//import com.polaris.lesscode.form.internal.req.AppFormSaveReq;
//import com.polaris.lesscode.permission.internal.model.OrgUserPermissionContext;
//import com.polaris.lesscode.permission.internal.model.req.InitAppPermissionReq;
//import com.polaris.lesscode.util.ConvertUtil;
//import com.polaris.lesscode.util.RpcUtil;
//import edp.davinci.internal.feign.DashboardProvider;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;

///**
// * @author: Liu.B.J
// * @data: 2020/10/20 17:28
// * @modified:
// */
//
//@Slf4j
//@Service
//public class AppOpenService extends ServiceImpl<AppMapper, App> {
//
//	@Autowired
//	private AppPackageService appPackageService;
//
//	@Autowired
//	private AppMapper appMapper;
//
//	@Autowired
//	private AppFormApi formProvider;
//
//	@Autowired
//	private DashboardProvider dashboardProvider;
//
//	@Autowired
//	private PermissionUtil permissionUtil;
//
//	@Transactional(rollbackFor = Exception.class)
//	public AppResp add(AppOpenAddReq req, Long orgId, Long userId) {
//		appPackageService.get(req.getPkgId());
//		OrgUserPermissionContext orgUserPermissionContext = new OrgUserPermissionContext(permissionUtil.getUserPermission(orgId, userId).getData());
//		if(! orgUserPermissionContext.hasManageAppPackagePermission(req.getPkgId())){
//			throw new BusinessException(ResultCode.NO_ADD_APP_PERMISSION.getCode(), ResultCode.NO_ADD_APP_PERMISSION.getMessage());
//		}
//		App app = new App();
//		app.setPkgId(req.getPkgId());
//		app.setSort(req.getSort());
//		app.setName(req.getName());
//		if(AppType.formatOrNull(req.getType()) == null || YesOrNo.formatOrNull(req.getHidden()) == null){
//			throw new BusinessException(ResultCode.UNSUPPORT_APP_TYPE.getCode(), ResultCode.UNSUPPORT_APP_TYPE.getMessage());
//		}
//		if(req.getAppId() != null){
//			app.setId(req.getAppId());
//		}
//		app.setType(req.getType());
//		app.setHidden(req.getHidden());
//		app.setIcon(req.getIcon());
//		app.setOrgId(orgId);
//		app.setCreator(userId);
//		app.setUpdator(userId);
//		//app.setSort(groupService.getMaxSort(orgId));
//		app.setExternalApp(YesOrNo.YES.getCode());
//		app.setLinkUrl(req.getLinkUrl());// 若为员工备忘录 路由写无码的
//		app.setDelFlag(CommonConsts.NO_DELETE);
//
//		boolean suc = appMapper.insert(app) > 0;
//		if(suc) {
//			AppResp appResp = ConvertUtil.convert(app, AppResp.class);
//			if(AppType.DASHBOARD.getCode().equals(req.getType()) && Objects.equals(req.getCreateTable(), Boolean.TRUE)) {
//				DashboardCreate dashboardCreate = new DashboardCreate();
//				dashboardCreate.setOrgId(orgId);
//				dashboardCreate.setDashboardPortalId(app.getId());
//				dashboardCreate.setAppId(app.getId());
//				dashboardCreate.setName(app.getName());
//				dashboardCreate.setType((short) 1);
//				DashboardResp resp = RpcUtil.accept(dashboardProvider.createDashboard(dashboardCreate));
//				appResp.setDashboardResp(resp);
//				log.info("创建仪表盘成功： {}", JSON.toJSONString(resp));
//			} else if((AppType.FORM.getCode().equals(req.getType()) || AppType.SUMMARY.getCode().equals(req.getType())) && Objects.equals(req.getCreateTable(), Boolean.TRUE)) {
//				AppFormSaveReq createAppFormReq = new AppFormSaveReq();
//				createAppFormReq.setAppId(app.getId());
//				createAppFormReq.setDrafted(false);
//				createAppFormReq.setOrgId(orgId);
//				createAppFormReq.setUserId(userId);
//				createAppFormReq.setConfig(req.getConfig());
//				createAppFormReq.setIsExt(req.getIsExt());
//				formProvider.saveForm(req.getPkgId(), req.getType(), createAppFormReq);

		// 更新字段
////				formProvider.saveForm(req.getPkgId(), req.getType(), createAppFormReq);
//				log.info("创建应用表单成功");
//			} else {
//				throw new BusinessException(ResultCode.UNSUPPORT_APP_TYPE);
//			}
//			InitAppPermissionReq initPerReq = new InitAppPermissionReq();
//			initPerReq.setOrgId(orgId);
//			initPerReq.setAppId(app.getId());
//			initPerReq.setUserId(userId);
//			initPerReq.setAppType(req.getType());
//			initPerReq.setConfig(req.getConfig());
//			initPerReq.setComponentType(req.getComponentType());
//			if(Objects.equals(req.getIsExt(), Boolean.TRUE)){
//				initPerReq.setIsExt(Boolean.TRUE);
//				initPerReq.setOptAuthOptions(req.getOptAuthOptions());
//				initPerReq.setFieldAuthOptions(req.getFieldAuthOptions());
//			}
//			boolean b = permissionUtil.initDefaultPermissionGroup(initPerReq).getData();
//			if(! b){
//				throw new BusinessException(ResultCode.INIT_FORM_PERMISSION_GROUP_FAIL.getCode(), ResultCode.INIT_FORM_PERMISSION_GROUP_FAIL.getMessage());
//			}
//			return appResp;
//		}
//		throw new BusinessException(ResultCode.APP_ADD_FAIL.getCode(), ResultCode.APP_ADD_FAIL.getMessage());
//	}
//
//	@Transactional(rollbackFor = Exception.class)
//	public AppResp update(Long appId, AppOpenUpdateReq req, Long orgId, Long userId) {
//		App oldApp = appMapper.get(orgId, appId);
//		if(oldApp == null) {
//			throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " org:" + orgId + " app:" + appId);
//		}
//
//		OrgUserPermissionContext orgUserPermissionContext = new OrgUserPermissionContext(permissionUtil.getUserPermission(orgId, userId).getData());
//		if(! orgUserPermissionContext.hasManageAppPackagePermission(oldApp.getPkgId())){
//			throw new BusinessException(ResultCode.NO_MODIFY_APP_PERMISSION.getCode(), ResultCode.NO_MODIFY_APP_PERMISSION.getMessage());
//		}
//		App app = new App();
//		app.setId(appId);
//		app.setName(req.getName());
//		if(AppType.formatOrNull(req.getType()) != null){
//			app.setType(req.getType());
//		}
//		if (YesOrNo.formatOrNull(req.getHidden()) != null) {
//			app.setHidden(req.getHidden());
//		}
//		app.setIcon(req.getIcon());
//		app.setUpdator(userId);
//		app.setLinkUrl(req.getLinkUrl());
//		app.setStatus(AppStatus.formatOrNull(req.getStatus()) == null ? AppStatus.USABLE.getCode() : req.getStatus());
//		log.info(">>>>>>>>>>>>>>>>>>>>>openApi>>>>编辑应用>>>>>>>>>>>>>update>>>>app={}", app);
//		appMapper.updateById(app);
//		if (!permissionUtil.updateFormFieldConfig(orgId, appId, req.getConfig())) {
//			throw new BusinessException(ResultCode.APP_FORM_FIELD_UPDATE_FAIL);
//		}
//
//		// 更新字段
//		AppFormSaveReq appFormSaveReq = new AppFormSaveReq();
//		appFormSaveReq.setConfig(req.getConfig());
//		appFormSaveReq.setAppId(appId);
//		appFormSaveReq.setDrafted(false);
//		appFormSaveReq.setOrgId(orgId);
//		appFormSaveReq.setUserId(userId);
//		formProvider.saveForm(oldApp.getPkgId(), oldApp.getType(), appFormSaveReq);

////		formProvider.saveForm(oldApp.getPkgId(), oldApp.getType(), appFormSaveReq);
//
//		AppResp resp = ConvertUtil.convert(app, AppResp.class);
//		return resp;
//	}
//
//	public boolean delete(Long id, Long orgId, Long userId) {
//		App oldApp = appMapper.get(orgId, id);
//		if (oldApp == null) {
//			throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " org:" + orgId + " app:" + id);
//		}
//		OrgUserPermissionContext orgUserPermissionContext = new OrgUserPermissionContext(permissionUtil.getUserPermission(orgId, userId).getData());
//		if (!orgUserPermissionContext.hasManageAppPackagePermission(oldApp.getPkgId())) {
//			throw new BusinessException(ResultCode.NO_DELETE_APPPKG_PERMISSION.getCode(), ResultCode.NO_DELETE_APPPKG_PERMISSION.getMessage());
//		}
//		boolean b = permissionUtil.deleteAppPermission(id, orgId, userId);
//		if (!b) {
//			throw new BusinessException(ResultCode.APP_DEL_PERMISSION_GROUP_FAIL.getCode(), ResultCode.APP_DEL_PERMISSION_GROUP_FAIL.getMessage());
//		}
//
//		oldApp.setDelFlag(CommonConsts.DELETED);
//		oldApp.setUpdator(userId);
//
//		boolean suc = appMapper.updateById(oldApp) > 0;
//		if (!suc) {
//			throw new BusinessException(ResultCode.APP_MODIFY_FAIL.getCode(), ResultCode.APP_MODIFY_FAIL.getMessage());
//		}
//		List<Long> appIds = new ArrayList<>();
//		appIds.add(id);
////		formProvider.deleteForm(appIds, userId);
//		return suc;
//	}
//
//}
