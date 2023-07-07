package com.polaris.lesscode.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.filter.IFilterConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.polaris.lesscode.app.bo.*;
import com.polaris.lesscode.app.consts.AppConsts;
import com.polaris.lesscode.app.consts.PermissionConsts;
import com.polaris.lesscode.app.consts.ThreadPools;
import com.polaris.lesscode.app.consts.UserConsts;
import com.polaris.lesscode.app.entity.*;
import com.polaris.lesscode.app.entity.AppTemplate;
import com.polaris.lesscode.app.enums.AppTemplateCategoryCode;
import com.polaris.lesscode.app.enums.AppTemplateType;
import com.polaris.lesscode.app.enums.TemplateResourceType;
import com.polaris.lesscode.app.internal.enums.AppRelationType;
import com.polaris.lesscode.app.internal.enums.AppType;
import com.polaris.lesscode.app.mapper.*;
import com.polaris.lesscode.app.req.ApplyTemplateReq;
import com.polaris.lesscode.app.req.CreateTemplateReq;
import com.polaris.lesscode.app.resp.*;
import com.polaris.lesscode.app.utils.PermissionUtil;
import com.polaris.lesscode.app.vo.ResultCode;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.dashboard.internal.req.DashboardAppTemplateReq;
import com.polaris.lesscode.dashboard.internal.req.DashboardTemplate;
import com.polaris.lesscode.dashboard.internal.resp.DashboardResp;
import com.polaris.lesscode.dashboard.internal.resp.DashboardWidgetResp;
import com.polaris.lesscode.dashboard.internal.resp.WidgetResp;
import com.polaris.lesscode.dc.internal.api.DataCenterApi;
import com.polaris.lesscode.dc.internal.dsl.*;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.internal.api.AppFormApi;
import com.polaris.lesscode.form.internal.feign.AppFormProvider;
import com.polaris.lesscode.form.internal.resp.AppFormResp;
import com.polaris.lesscode.form.internal.sula.FormJson;
import com.polaris.lesscode.gotable.internal.feign.GoTableProvider;
import com.polaris.lesscode.gotable.internal.req.CopyTablesRequest;
import com.polaris.lesscode.gotable.internal.req.ReadTablesRequest;
import com.polaris.lesscode.gotable.internal.resp.CopyTablesResp;
import com.polaris.lesscode.gotable.internal.resp.ReadTablesResp;
import com.polaris.lesscode.permission.internal.api.AppPermissionApi;
import com.polaris.lesscode.permission.internal.api.PermissionApi;
import com.polaris.lesscode.permission.internal.enums.AppPerDefaultGroupLangCode;
import com.polaris.lesscode.permission.internal.enums.DefaultAppPermissionGroupType;
import com.polaris.lesscode.permission.internal.feign.AppPermissionProvider;
import com.polaris.lesscode.permission.internal.model.UserPermissionVO;
import com.polaris.lesscode.permission.internal.model.req.AddAppMembersReq;
import com.polaris.lesscode.permission.internal.model.req.CreateAppPermissionGroupReq;
import com.polaris.lesscode.permission.internal.model.req.InitAppPermissionReq;
import com.polaris.lesscode.permission.internal.model.resp.AppPermissionGroupResp;
import com.polaris.lesscode.project.internal.api.ProjectApi;
import com.polaris.lesscode.project.internal.req.AppEvent;
import com.polaris.lesscode.project.internal.req.DeleteProjectBatchInnerReq;
import com.polaris.lesscode.project.internal.resp.ApplyProjectTemplateResp;
import com.polaris.lesscode.project.internal.resp.ProjectTemplate;
import com.polaris.lesscode.project.internal.resp.TemplateInfo;
import com.polaris.lesscode.uc.internal.api.UserCenterApi;
import com.polaris.lesscode.uc.internal.req.UserAuthorityReq;
import com.polaris.lesscode.uc.internal.req.UserListByIdsReq;
import com.polaris.lesscode.uc.internal.resp.UserAuthorityResp;
import com.polaris.lesscode.uc.internal.resp.UserInfoResp;
import com.polaris.lesscode.util.*;
import edp.davinci.internal.api.DashboardApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用模板
 *
 * @Author Nico
 * @Date 2021/3/16 15:54
 **/
@Slf4j
@Service
public class AppTemplateService extends ServiceImpl<AppTemplateMapper, AppTemplate> {

    @Autowired
    private AppTemplateMapper appTemplateMapper;

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private AppRelationMapper appRelationMapper;

//    @Autowired
//    private AppFormProvider appFormProvider;

    @Autowired
    private GoTableProvider goTableProvider;

    @Autowired
    private AppPackageMapper appPackageMapper;

    @Autowired
    private AppPermissionProvider appPermissionProvider;

    @Autowired
    private AppViewMapper appViewMapper;

    @Autowired
    private AppViewService appViewService;

    @Autowired
    private DashboardApi dashboardApi;

    @Autowired
    private ProjectApi projectApi;

    @Autowired
    private DataCenterApi dataCenterApi;

    @Autowired
    private PermissionApi permissionApi;

    @Autowired
    private AppService appService;

    @Autowired
    private AppTemplateCateRelateMapper appTemplateCateRelateMapper;

    @Autowired
    private AppTemplateCateMapper appTemplateCateMapper;

    @Autowired
    private AppTemplateApplyLogMapper appTemplateApplyLogMapper;

    @Autowired
    private UserCenterApi userCenterApi;

    @Autowired
    private AppPermissionApi appPermissionApi;

    @Autowired
    private GroupService groupService;

    @Autowired
    private PermissionUtil permissionUtil;

    /**
     * 创建模板
     *
     * @param req {@link CreateTemplateReq}
     * @return {@link AppTemplateResp}
     **/
    //@Transactional(rollbackFor = Exception.class)
    public AppTemplateResp create(Long orgId, Long userId, CreateTemplateReq req){
        log.info("[AppTemplateService] [create], orgId: {}, userId: {}, req: {}", orgId, userId, req);
        Long uploadOrgId = orgId;
        if (Objects.equals(req.getUsableRange(), 1)){
            orgId = AppConsts.PUBLIC_TEMPLATE_ORG_ID;
        }

        UserAuthorityReq userAuthorityReq = new UserAuthorityReq();
        userAuthorityReq.setUserId(userId);
        userAuthorityReq.setOrgId(uploadOrgId);
        UserAuthorityResp userAuthorityResp = userCenterApi.getUserAuthority(userAuthorityReq).getData();
        if (! Objects.equals(userAuthorityResp.getIsSysAdmin(), true) &&
                ! Objects.equals(userAuthorityResp.getIsOrgOwner(), true) &&
                ! userAuthorityResp.getOptAuth().contains(PermissionConsts.TPL_SAVE_AS)){
            throw new BusinessException(ResultCode.FORBIDDEN_ACCESS);
        }

        UserListByIdsReq userListByIdsReq = new UserListByIdsReq();
        userListByIdsReq.setOrgId(uploadOrgId);
        userListByIdsReq.setIds(Collections.singletonList(userId));
        List<UserInfoResp> uploaders = userCenterApi.getAllUserListByIds(userListByIdsReq).getData();

        AppTemplate appTemplate = new AppTemplate();
        appTemplate.setCover(req.getCover());
        appTemplate.setName(req.getName());
        appTemplate.setRemark(req.getRemark());
        appTemplate.setTplStatus(2);
        appTemplate.setType(AppTemplateType.PRIVATE.getCode());
        appTemplate.setCreator(userId);
        appTemplate.setUpdator(userId);
        appTemplate.setOrgId(orgId);
        if (Objects.equals(req.getUsableRange(), 1)) {
            appTemplate.setIsShow(0);
        } else {
            appTemplate.setIsShow(1);
        }
        appTemplate.setUploadOrgId(uploadOrgId);
        if (uploaders.size() > 0) {
            appTemplate.setUploader(JSON.toJSONString(uploaders.get(0)));
        }
        Set<String> resources = Objects.isNull(req.getResources()) ? TemplateResourceType.ALL_RESOURCES : req.getResources();
        AppTemplateConfig config = parseConfig(uploadOrgId, userId, req.getAppId(), resources, appTemplate.getCover());
        if (config == null){
            throw new BusinessException(ResultCode.APP_TEMPLATE_CONFIG_IS_EMPTY);
        }
        Integer saveSampleFlag = 1;
        if (!resources.contains(TemplateResourceType.RECORDS.getCode())){
            saveSampleFlag = 2;
        }
        List<AppResp> apply = apply(orgId, uploadOrgId, userId, appTemplate.getId(), config, true, saveSampleFlag, false, true);
        ArrayList<Long> sampleAppIds = new ArrayList<>();
        apply.forEach(appResp -> {
            sampleAppIds.add(appResp.getId());
        });
        if (CollectionUtils.isEmpty(sampleAppIds)){
            throw new BusinessException(ResultCode.APP_TEMPLATE_CREATE_FAIL);
        }
        appTemplate.setConfig(GsonUtils.toJson(sampleAppIds));
        appTemplateMapper.insert(appTemplate);

        if (Objects.equals(req.getUsableRange(), 1)) {
            AppTemplate appTemplateFrom = appTemplateMapper.selectOne(new QueryWrapper<AppTemplate>()
                    .eq("JSON_EXTRACT(config, '$[0]')", req.getAppId())
                    .lambda().eq(AppTemplate::getOrgId, uploadOrgId)
                    .eq(AppTemplate::getDelFlag, CommonConsts.FALSE));
            if (appTemplateFrom != null) {
                appTemplateFrom.setIsUploaded(1);
                appTemplateMapper.updateById(appTemplateFrom);
            }
        }

        AppTemplateResp appTemplateResp = new AppTemplateResp();
        appTemplateResp.setName(appTemplate.getName());
        appTemplateResp.setId(appTemplate.getId());
        appTemplateResp.setCover(appTemplate.getCover());
        appTemplateResp.setRemark(appTemplate.getRemark());
        return appTemplateResp;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long orgId, Long userId, Long tplId){
        UserAuthorityReq userAuthorityReq = new UserAuthorityReq();
        userAuthorityReq.setUserId(userId);
        userAuthorityReq.setOrgId(orgId);
        UserAuthorityResp userAuthorityResp = userCenterApi.getUserAuthority(userAuthorityReq).getData();
        if (! Objects.equals(userAuthorityResp.getIsSysAdmin(), true) &&
                ! Objects.equals(userAuthorityResp.getIsOrgOwner(), true) &&
                ! userAuthorityResp.getOptAuth().contains(PermissionConsts.TPL_DELETE)){
            throw new BusinessException(ResultCode.FORBIDDEN_ACCESS);
        }

        AppTemplate appTemplate = appTemplateMapper.selectOne(new LambdaQueryWrapper<AppTemplate>()
                .eq(AppTemplate::getId, tplId)
                .eq(AppTemplate::getOrgId, orgId)
                .eq(AppTemplate::getDelFlag, CommonConsts.FALSE));
        if (appTemplate == null){
            throw new BusinessException(ResultCode.APP_TEMPLATE_NOT_EXIST);
        }
        List<Long> sampleAppIds = JSON.parseArray(appTemplate.getConfig(), Long.class);
        if (CollectionUtils.isNotEmpty(sampleAppIds)){
            List<App> sampleApps = appMapper.selectList(new LambdaQueryWrapper<App>().in(App::getId, sampleAppIds));
            if (CollectionUtils.isNotEmpty(sampleApps)){
                // 删除项目
                List<Long> deletedProjectIds = sampleApps.stream().filter(app -> Objects.nonNull(app.getProjectId()) && app.getProjectId() > 0).map(App::getProjectId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(deletedProjectIds)){
                    projectApi.deleteProjectBatchInner(orgId, userId, deletedProjectIds);
                }
                // 删除应用
                appMapper.update(null, new LambdaUpdateWrapper<App>().in(App::getId, sampleAppIds).set(App::getDelFlag, CommonConsts.TRUE));

                Long projectId = deletedProjectIds.get(0);
                Long appId = sampleAppIds.get(0);
                // 事件上报
                ThreadPools.POOLS.execute(() -> {
                    AppEvent appEvent = new AppEvent();
                    appEvent.setOrgId(orgId);
                    appEvent.setAppId(appId);
                    appEvent.setProjectId(projectId);
                    appEvent.setUserId(userId);
                    projectApi.reportAppEvent(AppConsts.EventTypeAppDeleted, "", appEvent);
                });
            }
        }
        AppTemplate updated = new AppTemplate();
        updated.setId(appTemplate.getId());
        updated.setDelFlag(CommonConsts.TRUE);
        appTemplateMapper.updateById(updated);
        return true;
    }

    /**
     * 应用模板，暂时默认全都是表单
     *
     * @param orgId 组织id
     * @param userId 操作人id
     **/
    //@Transactional(rollbackFor = Throwable.class)
    public List<AppResp> apply(Long orgId, Long userId, Long templateId, Long parentId, boolean isSample, Integer saveSampleFlag, boolean isNewbieGuide)  {
        AppTemplate appTemplate = appTemplateMapper.selectById(templateId);
        if (Objects.isNull(appTemplate)){
            throw new BusinessException(ResultCode.APP_TEMPLATE_NOT_EXIST);
        }
        if (! Objects.equals(appTemplate.getOrgId(), AppConsts.PUBLIC_TEMPLATE_ORG_ID) && ! Objects.equals(appTemplate.getOrgId(), orgId)){
            throw new BusinessException(ResultCode.APP_TEMPLATE_NOT_EXIST);
        }
        List<Long> sampleAppIds = JSON.parseArray(appTemplate.getConfig(), Long.class);
        if (CollectionUtils.isEmpty(sampleAppIds)){
            throw new BusinessException(ResultCode.APP_TEMPLATE_CONFIG_IS_EMPTY);
        }
        AppTemplateConfig templateConfig = parseConfig(sampleAppIds,orgId,userId, appTemplate.getCover());
        List<AppResp> applyResp = apply(orgId, appTemplate.getOrgId(), userId, templateId, templateConfig, isSample, saveSampleFlag, isNewbieGuide, false);
        ArrayList<Long> appIds = new ArrayList<>();
        applyResp.forEach(appResp -> {
            appIds.add(appResp.getId());
        });
        if (CollectionUtils.isNotEmpty(appIds) && parentId != null && parentId > 0){
            appMapper.update(null, new LambdaUpdateWrapper<App>().in(App::getId, appIds).set(App::getParentId, parentId));
        }
        return applyResp;
    }

    public List<AppResp> apply(Long orgId, Long fromOrgId, Long userId, Long templateId, AppTemplateConfig templateConfig,
                               boolean isSample, Integer saveSampleFlag, boolean isNewbieGuide, boolean isCreateTemplate)  {
        List<com.polaris.lesscode.app.bo.AppTemplate> appTemplates = templateConfig.getAppTemplates();
        LinkedList<Long> appIds = new LinkedList<>();
        ArrayList<App> appList = new ArrayList<>();
        // 获取企业汇总表
        App orgSummaryApp = appMapper.selectOne(new LambdaQueryWrapper<App>()
                .eq(App::getOrgId, orgId)
                .eq(App::getDelFlag, CommonConsts.FALSE)
                .eq(App::getType, AppType.SUMMARY.getCode())
                .last("limit 1"));
        HashMap<Long, App> appMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(appTemplates)){
            Map<Long, Long> appIdMap = new HashMap<>();
            Map<Long, Long> viewIdMap = new HashMap<>();
            Map<Long, Long> widgetIdMap = new HashMap<>();
            for (com.polaris.lesscode.app.bo.AppTemplate template: appTemplates){
                appIdMap.put(template.getId(), IdWorker.getId());
                if (CollectionUtils.isNotEmpty(template.getAppViewTemplates())){
                    for (AppViewTemplate appViewTemplate: template.getAppViewTemplates()){
                        viewIdMap.put(appViewTemplate.getId(), IdWorker.getId());
                    }
                }
            }
            Long rootId = appIdMap.get(appTemplates.get(0).getId());
            // 保证汇总表先初始化
            appTemplates.sort((o1, o2) -> o2.getType().compareTo(o1.getType()));
            for (com.polaris.lesscode.app.bo.AppTemplate template: appTemplates){
                template.setNeedData(true);
                if (Objects.equals(saveSampleFlag, 2)){ // 不保留
                    template.setNeedData(false);
                    template.setDataTemplate(null);
                    if (Objects.nonNull(template.getAppProjectTemplate())){
                        template.getAppProjectTemplate().setProjectIterationTemplates(null);
                    }
                }
                App ret = applyAppTemplate(orgId, fromOrgId, userId, template, appIdMap, widgetIdMap, viewIdMap, isSample, isNewbieGuide, isCreateTemplate,orgSummaryApp);
                appIds.add(ret.getId());
                appMap.put(ret.getId(), ret);
            }
            // rootid放第一个
            appIds.remove(rootId);
            appIds.addFirst(rootId);
        }
        if (! isSample){
            appTemplateMapper.addHot(templateId);

            AppTemplateApplyLog applyLog = new AppTemplateApplyLog();
            applyLog.setOrgId(orgId);
            applyLog.setTplId(templateId);
            applyLog.setCreator(userId);
            applyLog.setUpdator(userId);
            appTemplateApplyLogMapper.insertLog(applyLog);
        }
        appIds.forEach(appId->{
            App app = appMap.get(appId);
            appList.add(app);
        });
        return ConvertUtil.convertList(appList, AppResp.class);
    }

    //@Transactional(rollbackFor = Exception.class)
    App applyAppTemplate(Long orgId, Long fromOrgId, Long userId, com.polaris.lesscode.app.bo.AppTemplate template,
                         Map<Long, Long> appIdMap, Map<Long, Long> widgetIdMap, Map<Long, Long> viewIdMap,
                         boolean isSample, boolean isNewbieGuide, boolean isCreateTemplate, App orgSummaryApp){
        // 首先做一些必要的判断，避免中途失败需要回滚（在系统支持分布式事务之前的权宜之计）
        if (!Objects.equals(orgId, AppConsts.PUBLIC_TEMPLATE_ORG_ID) &&
                template.getAppProjectTemplate() != null) {
            projectApi.authCreateProject(orgId);
        }

        App app = new App();
        app.setId(appIdMap.get(template.getId()));
        if (Objects.nonNull(template.getParentId()) && template.getParentId() > 0){
            app.setParentId(appIdMap.get(template.getParentId()));
        }
        if (Objects.nonNull(template.getExtendsId()) && template.getExtendsId() > 0){
            if (appIdMap.containsKey(template.getExtendsId())){
                app.setExtendsId(appIdMap.get(template.getExtendsId()));
            }else if (orgSummaryApp != null){
                app.setExtendsId(orgSummaryApp.getId());
            }else{
                app.setExtendsId(template.getExtendsId());
            }
        }
        // 镜像
        if (template.getMirrorAppId() != null && template.getMirrorAppId() > 0){
            if (appIdMap.containsKey(template.getMirrorAppId())){
                app.setMirrorAppId(appIdMap.get(template.getMirrorAppId()));
                app.setMirrorViewId(viewIdMap.get(template.getMirrorViewId()));
            }else{
                app.setMirrorAppId(template.getMirrorAppId());
                app.setMirrorViewId(template.getMirrorViewId());
            }
        }

        int templateFlag = isSample ? CommonConsts.TRUE : CommonConsts.FALSE;
        // 模板示例项目
        app.setTemplateFlag(templateFlag);
        app.setOrgId(orgId);
        app.setName(template.getName());
        app.setIcon(template.getIcon());
        app.setType(template.getType());
        app.setCreator(userId);
        app.setAuthType(2); // 自定义权限
        app.setSort(groupService.getMinSort(orgId) - 1);
        app.setUpdator(userId);
        appMapper.insert(app);

        // 添加创建人为成员
        if (userId > 0) {
            AppRelation appRelation = new AppRelation();
            appRelation.setAppId(app.getId());
            appRelation.setRelationId(userId);
            appRelation.setType(AppRelationType.USER.getCode());
            appRelation.setOrgId(app.getOrgId());
            appRelation.setCreator(app.getCreator());
            appRelation.setUpdator(app.getUpdator());
            appRelationMapper.insert(appRelation);
        }

        // 角色
        Map<String, Long> nameIdMap = new HashMap<>();
        Map<Long,Long> oldToNewPermission = new HashMap<>();
        if (CollectionUtils.isNotEmpty(template.getAppPermissionGroupsTemplates())){
            for (AppPermissionGroupResp appPermissionGroupResp: template.getAppPermissionGroupsTemplates()){
                appPermissionGroupResp.setAppId(app.getId());
                appPermissionGroupResp.setOrgId(app.getOrgId());
                appPermissionGroupResp.setCreator(userId);
                appPermissionGroupResp.setUpdator(userId);
                nameIdMap.put(appPermissionGroupResp.getName(), appPermissionGroupResp.getId());
            }

            List<AppPermissionGroupResp> appPermissionGroupRespList = appPermissionApi.createAppPermissionGroup(ConvertUtil.convertList(template.getAppPermissionGroupsTemplates(), CreateAppPermissionGroupReq.class)).getData();
            appPermissionGroupRespList.forEach(item->{
                Long oldId = nameIdMap.get(item.getName());
                if (!Objects.isNull(oldId)) {
                    oldToNewPermission.put(oldId, item.getId());
                }

                // 把创建人加为管理员
                if (item.getLangCode().equals(AppPerDefaultGroupLangCode.OWNER.getCode()) ||
                        item.getLangCode().equals(AppPerDefaultGroupLangCode.FORM_ADMINISTRATOR.getCode()) ||
                        item.getLangCode().equals(AppPerDefaultGroupLangCode.DASHBOARD_ADMINISTRATOR.getCode())) {
                    List<String> memberIds = new ArrayList<>();
                    memberIds.add(AppConsts.MEMBER_USER_TYPE + userId.toString());
                    AddAppMembersReq addAppMembersReq = new AddAppMembersReq();
                    addAppMembersReq.setOrgId(orgId);
                    addAppMembersReq.setAppId(app.getId());
                    addAppMembersReq.setMemberIds(memberIds);
                    addAppMembersReq.setPerGroupId(item.getId());
                    log.info("[createApp] 添加创建人到权限组: {}", addAppMembersReq);
                    boolean suc = permissionUtil.addAppMembers(addAppMembersReq);
                    if(! suc) {
                        throw new BusinessException(ResultCode.INIT_APP_CREATOR_PERMISSION_GROUP_FAIL);
                    }
                }
            });
        }

        // 初始化应用表单
        CopyTablesResp copyTablesResp = goTableProvider.copyTables(new CopyTablesRequest(template.getId(), null, app.getId(), oldToNewPermission), orgId.toString(), userId.toString());

        // 初始化项目
        if (template.getAppProjectTemplate() != null){
            ProjectTemplate projectTemplate = template.getAppProjectTemplate();
            projectTemplate.setProjectIssueTemplates(template.getDataTemplate());
            projectTemplate.setTemplateFlag(templateFlag);
            projectTemplate.setIsNewbieGuide(isNewbieGuide);
            projectTemplate.setIsCreateTemplate(isCreateTemplate);
            projectTemplate.setNeedData(template.isNeedData());
            projectTemplate.setFromOrgId(fromOrgId);
            log.info("projectTemplate json {}", JsonUtils.toJson(projectTemplate));
            ApplyProjectTemplateResp applyProjectTemplateResp = projectApi.applyProjectTemplateInner(orgId, userId, app.getId(), projectTemplate).getData();
            app.setProjectId(applyProjectTemplateResp.getProjectId());
            if (Objects.nonNull(applyProjectTemplateResp.getProjectId())){
                App updateApp = new App();
                updateApp.setId(app.getId());
                updateApp.setProjectId(applyProjectTemplateResp.getProjectId());
                appMapper.updateById(updateApp);
            }
        }

        // 复制仪表盘
        DashboardAppTemplateReq dashboardAppTemplateReq = new DashboardAppTemplateReq();
        dashboardAppTemplateReq.setAppId(app.getId());
        dashboardAppTemplateReq.setOrgId(app.getOrgId());
        dashboardAppTemplateReq.setUserId(userId);
        dashboardAppTemplateReq.setOldToNewTables(copyTablesResp.getOldToNewTableId());
        dashboardAppTemplateReq.setOldAppId(template.getId());
        dashboardApi.applyTemplate(dashboardAppTemplateReq);

        // 考虑汇总表
        final boolean hasExtendsId = Objects.nonNull(app.getExtendsId()) && app.getExtendsId() > 0;

        // 初始化应用视图
        Map<Long, Long> oldToNewId = copyTablesResp.getOldToNewTableId();
        if (CollectionUtils.isNotEmpty(template.getAppViewTemplates())){
            List<AppView> appViews = new ArrayList<>();
            for (AppViewTemplate appViewTemplate: template.getAppViewTemplates()){
                AppView appView = new AppView();
                appView.setId(viewIdMap.get(appViewTemplate.getId()));
                appView.setAppId(app.getId());
                appView.setOrgId(app.getOrgId());
                appView.setType(appViewTemplate.getType());
                appView.setViewName(appViewTemplate.getName());
                appView.setConfig(appViewTemplate.getConfig());

                if (! MapUtils.isEmpty(oldToNewId)){
                    try {
                        Map config = JacksonUtils.readValue(appViewTemplate.getConfig(), Map.class);
                        log.info("replace id, config: {}", config);
                        if (config != null && config.containsKey("tableId")){
                            Long newTableId = oldToNewId.get(Long.parseLong(String.valueOf(config.get("tableId"))));
                            log.info("replace id, before newTableId: {}", config.get("tableId"));
                            log.info("replace id, newTableId: {}", newTableId);
                            if (Objects.nonNull(newTableId)){
                                config.put("tableId", newTableId.toString());
                                appView.setConfig(JacksonUtils.Obj2Str(config));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                appView.setCreator(userId);
                appView.setUpdator(userId);
                appView.setOwner(appViewTemplate.getOwner());
                appViews.add(appView);
            }
            appViewService.saveBatch(appViews);
        }
        return app;
    }

    /**
     * 查询模板列表
     **/
    public List<AppTemplateResp> list(Long orgId, Long categoryId, Long tagId, String name, Integer usageRange){
        QueryWrapper<AppTemplate> query = new QueryWrapper<AppTemplate>();
        // 使用范围
        if (Objects.equals(usageRange, 1)){
            query.eq("tpl_status", 1);
            query.eq("org_id", AppConsts.PUBLIC_TEMPLATE_ORG_ID);
        }else if (Objects.equals(usageRange, 2)){
            query.eq("org_id", orgId);
            // 团队模板新建的放在最前面
            query.orderByDesc("id");
        }else{
            query.eq("tpl_status", 1);
            query.in("org_id", Arrays.asList(orgId, AppConsts.PUBLIC_TEMPLATE_ORG_ID));
        }


        boolean isPopular = false;
        int feature = 0;
        // 排序
        if (Objects.nonNull(categoryId)){
            AppTemplateCate appTemplateCate = appTemplateCateMapper.selectById(categoryId);
            if (Objects.isNull(appTemplateCate)){
                throw new BusinessException(ResultCode.APP_TEMPLATE_CATE_NON_EXIST);
            }
            AppTemplateCategoryCode cateCode = AppTemplateCategoryCode.formatOrNull(appTemplateCate.getCode());
            if (cateCode != null){
                if (Objects.equals(cateCode, AppTemplateCategoryCode.HOT)){
                    query.orderByDesc("hot");
                    feature = 1;
                }else if (Objects.equals(cateCode, AppTemplateCategoryCode.NEWEST)){
                    query.orderByDesc("shelf_time");
                    feature = 2;
                }else if (Objects.equals(cateCode, AppTemplateCategoryCode.POPULAR)){
                    isPopular = true;
                    feature = 3;
                }
                // 三个特殊的不需要实际的关联
                categoryId = null;
            }
        }

        // 模板名称筛选
        boolean isSearch = false;
        if (StringUtils.isNotBlank(name)) {
            isSearch = true;
            name = name.toLowerCase(Locale.ROOT);
            // 模糊查询标签
            QueryWrapper<AppTemplateCate> cateQuery = new QueryWrapper<AppTemplateCate>().like("lower(name)", "%" + name + "%").eq("type", 2).eq("del_flag", CommonConsts.FALSE);
            List<AppTemplateCate> tags = appTemplateCateMapper.selectList(cateQuery);
            List<Long> queryTagIds = CollectionUtils.isNotEmpty(tags) ? tags.stream().map(AppTemplateCate::getId).collect(Collectors.toList()) : null;
            final List<Long> tplIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(queryTagIds)){
                LambdaQueryWrapper<AppTemplateCateRelate> relateQuery = new LambdaQueryWrapper<>();
                relateQuery.eq(AppTemplateCateRelate::getRelateType, 2).in(AppTemplateCateRelate::getRelateId, queryTagIds).eq(AppTemplateCateRelate::getDelFlag, CommonConsts.FALSE);
                List<AppTemplateCateRelate> relations = appTemplateCateRelateMapper.selectList(relateQuery);
                if (CollectionUtils.isNotEmpty(relations)){
                    tplIds.addAll(relations.stream().map(AppTemplateCateRelate::getTplId).collect(Collectors.toList()));
                }
            }
            String finalName = name;
            query.and(wrapper -> {
                wrapper.or().like("lower(name)", "%" + finalName + "%");
                if (CollectionUtils.isNotEmpty(tplIds)){
                    wrapper.or().in("id", tplIds);
                }
                return wrapper;
            });
        }

        // 分类和标签筛选
        if (Objects.nonNull(categoryId) || Objects.nonNull(tagId)){
            LambdaQueryWrapper<AppTemplateCateRelate> relateQuery = new LambdaQueryWrapper<>();
            final Long cateId = categoryId;
            relateQuery.and(wrapper -> {
                if (Objects.nonNull(cateId)){
                    wrapper.or().eq(AppTemplateCateRelate::getRelateType, 1).eq(AppTemplateCateRelate::getRelateId, cateId);
                }
                if (Objects.nonNull(tagId)){
                    wrapper.or().eq(AppTemplateCateRelate::getRelateType, 2).eq(AppTemplateCateRelate::getRelateId, tagId);
                }
                return wrapper;
            });
            relateQuery.eq(AppTemplateCateRelate::getDelFlag, CommonConsts.FALSE);
            List<AppTemplateCateRelate> relations = appTemplateCateRelateMapper.selectList(relateQuery);
            if (CollectionUtils.isEmpty(relations)){
                return new ArrayList<>();
            }
            query.in("id", relations.stream().map(AppTemplateCateRelate::getTplId).collect(Collectors.toSet()));
        }

        query.eq("del_flag", CommonConsts.FALSE);
        if (!isSearch) {
            query.eq("is_show", 1);
        }
        query.eq("status", CommonConsts.TRUE);
        List<AppTemplate> appTemplates = appTemplateMapper.selectList(query.select(AppTemplate.class,info -> true));

        if (CollectionUtils.isNotEmpty(appTemplates)){
            Map<Long, AppTemplateCate> cateMap = new HashMap<>();
            Map<Long, List<AppTemplateCateRelate>> appTemplateTagMap = new HashMap<>();
            Map<Long, List<AppTemplateCateRelate>> appTemplateCateMap = new HashMap<>();
            Map<Long, Long> hots7 = new HashMap<>();
            List<Long> tplIds = appTemplates.stream().map(AppTemplate::getId).collect(Collectors.toList());
            // 查询模板标签
            List<AppTemplateCateRelate> tagRelations = appTemplateCateRelateMapper.selectList(new LambdaQueryWrapper<AppTemplateCateRelate>()
                    .in(AppTemplateCateRelate::getTplId, tplIds)
                    .eq(AppTemplateCateRelate::getDelFlag, CommonConsts.FALSE));
            if (CollectionUtils.isNotEmpty(tagRelations)){
                List<AppTemplateCate> appTemplateCates = appTemplateCateMapper.selectList(new LambdaQueryWrapper<AppTemplateCate>()
                        .in(AppTemplateCate::getId, tagRelations.stream().map(AppTemplateCateRelate::getRelateId).collect(Collectors.toList()))
                        .eq(AppTemplateCate::getDelFlag, CommonConsts.FALSE));
                if (CollectionUtils.isNotEmpty(appTemplateCates)){
                    cateMap.putAll(MapUtils.toMap(AppTemplateCate::getId, appTemplateCates));
                    appTemplateTagMap.putAll(tagRelations.stream().filter(t-> Objects.equals(t.getRelateType(), 2)).collect(Collectors.groupingBy(AppTemplateCateRelate::getTplId)));
                    appTemplateCateMap.putAll(tagRelations.stream().filter(t-> Objects.equals(t.getRelateType(), 1)).collect(Collectors.groupingBy(AppTemplateCateRelate::getTplId)));
                }
            }
            List<Map<String, Object>> results = appTemplateApplyLogMapper.selectMaps(new QueryWrapper<AppTemplateApplyLog>().select("count(*) as count,tpl_id as tplId").in("tpl_id", tplIds).groupBy("tpl_id"));
            if (CollectionUtils.isNotEmpty(results)){
                for (Map<String, Object> result: results){
                    if (StringUtils.isNumeric(String.valueOf(result.get("tplId"))) && StringUtils.isNumeric(String.valueOf(result.get("count")))){
                        hots7.put(Long.parseLong(String.valueOf(result.get("tplId"))), Long.parseLong(String.valueOf(result.get("count"))));
                    }
                }
            }
            final String templateType = Objects.equals(orgId, AppConsts.PUBLIC_TEMPLATE_ORG_ID) ? AppTemplateType.PUBLIC.getCode() : AppTemplateType.PRIVATE.getCode();

            List<Long> creatorIds = appTemplates.stream().filter(at -> ! Objects.equals(at.getOrgId(), AppConsts.PUBLIC_TEMPLATE_ORG_ID)).map(AppTemplate::getCreator).collect(Collectors.toList());
            UserListByIdsReq userListByIdsReq = new UserListByIdsReq();
            userListByIdsReq.setOrgId(orgId);
            userListByIdsReq.setIds(creatorIds);
            List<UserInfoResp> creators = userCenterApi.getAllUserListByIds(userListByIdsReq).getData();
            final Map<Long, UserInfoResp> creatorMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(creators)){
                creatorMap.putAll(MapUtils.toMap(UserInfoResp::getId, creators));
            }
            appTemplates.forEach(t -> {
                if (t.getUploader() != null && !t.getUploader().isEmpty()) {
                    UserInfoResp uploader = JSON.parseObject(t.getUploader(), UserInfoResp.class);
                    creatorMap.put(uploader.getId(), uploader);
                }
            });

            List<AppTemplateResp> respList = appTemplates.stream().map(t -> {
                AppTemplateResp appTemplateResp = new AppTemplateResp();
                appTemplateResp.setName(t.getName());
                appTemplateResp.setId(t.getId());
                appTemplateResp.setOrgId(t.getOrgId());
                appTemplateResp.setType(templateType);
                appTemplateResp.setCover(t.getCover());
                appTemplateResp.setRemark(t.getRemark());
                appTemplateResp.setSolution(t.getSolution());
                appTemplateResp.setUsableLevel(t.getUsableLevel());
                appTemplateResp.setHot(t.getHot());
                appTemplateResp.setShelfTime(t.getShelfTime());
                appTemplateResp.setTplStatus(t.getTplStatus());
                appTemplateResp.setHot7(hots7.getOrDefault(t.getId(), 0L));
                appTemplateResp.setAuthor(creatorMap.getOrDefault(t.getCreator(), UserConsts.SYSTEM_USER));
                appTemplateResp.setIsUploaded(t.getIsUploaded());
                List<Long> appIds = JSON.parseArray(t.getConfig(), Long.class);
                if (!CollectionUtils.isEmpty(appIds)) {
                    appTemplateResp.setAppId(appIds.get(0));
                }
                // 封装标签
                List<AppTemplateCateRelate> relateTags = appTemplateTagMap.get(t.getId());
                if (CollectionUtils.isNotEmpty(relateTags)){
                    List<AppTemplateCateResp> cateRespList = new ArrayList<>();
                    for (AppTemplateCateRelate relateTag: relateTags){
                        AppTemplateCate cate = cateMap.get(relateTag.getRelateId());
                        if (Objects.isNull(cate)){
                            continue;
                        }
                        cateRespList.add(ConvertUtil.convert(cate, AppTemplateCateResp.class));
                    }
                    appTemplateResp.setTags(cateRespList);
                }
                // 封装分类
                List<AppTemplateCateRelate> relateCates = appTemplateCateMap.get(t.getId());
                if (CollectionUtils.isNotEmpty(relateCates)){
                    appTemplateResp.setCategoryId(relateCates.get(0).getRelateId());
                }else{
                    appTemplateResp.setCategoryId(0L);
                }
                return appTemplateResp;
            }).collect(Collectors.toList());

            // 热度降序
            if (isPopular){
                respList.sort((o1, o2) -> o2.getHot7().compareTo(o1.getHot7()));
            }

            if (feature == 0){
                Date now = new Date();
                for (AppTemplateResp appTemplateResp: respList){
                    // 指标先写死
                    if (appTemplateResp.getHot7() > 10){
                        appTemplateResp.setFeature(3);
                    } else if (now.getTime() - appTemplateResp.getShelfTime().getTime() < AppConsts.DAY7){
                        appTemplateResp.setFeature(2);
                    } else if (appTemplateResp.getHot() > 10){
                        appTemplateResp.setFeature(1);
                    }
                }
            }
            return respList;
        }
        return new ArrayList<>();
    }

    public List<AppTemplateResp> recents(Long orgId, Long userId, Integer size){
        if (size == null || size < 0 || size > 100){
            size = 30;
        }
        List<AppTemplateApplyLog> logs = appTemplateApplyLogMapper.selectList(new QueryWrapper<AppTemplateApplyLog>().eq("org_id", orgId).eq("creator", userId).last("limit " + size).orderByDesc("update_time"));
        if (CollectionUtils.isEmpty(logs)){
            return new ArrayList<>();
        }
        List<AppTemplate> appTemplates = appTemplateMapper.selectList(new LambdaQueryWrapper<AppTemplate>()
                .select(AppTemplate.class, info -> !info.getColumn().equals("config"))
                .eq(AppTemplate::getDelFlag, CommonConsts.FALSE)
                .in(AppTemplate::getId, logs.stream().map(AppTemplateApplyLog::getTplId).collect(Collectors.toList())));
        Map<Long, Integer> indexes = new HashMap<>();
        for (int i = 0; i < logs.size(); i ++){
            indexes.put(logs.get(i).getTplId(), i);
        }
        appTemplates.sort(Comparator.comparing(o -> indexes.getOrDefault(o.getId(), 0)));
        return ConvertUtil.convertList(appTemplates, AppTemplateResp.class);
    }

    /**
     * 获取配置
     **/
    private AppTemplateConfig parseConfig(Long orgId, Long userId, Long appId, Set<String> resources, String cover){
        List<App> allApps = getSubApps(orgId, appId);
        if (CollectionUtils.isEmpty(allApps)){
            return null;
        }
        UserPermissionVO userPermissionVO = permissionApi.getUserPermission(orgId, userId).getData();
        // 做权限处理
        if (! userPermissionVO.getIsOrgOwner()
                && ! userPermissionVO.getIsSysAdmin()
                && ! userPermissionVO.getIsSubAdmin()){
            Set<Long> hasViewAppIds = appService.getUserVisibleAppIds(userPermissionVO);
            if (CollectionUtils.isEmpty(hasViewAppIds)){
                return null;
            }
            allApps.removeIf(app -> ! hasViewAppIds.contains(app.getId()));
        }
        AppTemplateConfig appTemplateConfig = new AppTemplateConfig();
        appTemplateConfig.setAppTemplates(buildTemplates(allApps, Objects.isNull(resources) ? TemplateResourceType.ALL_RESOURCES : resources, orgId, userId, cover));
        return appTemplateConfig;
    }

    private AppTemplateConfig parseConfig(List<Long> appIds, Long orgId, Long userId, String cover){
        List<App> apps = appMapper.getListByIds(appIds);
        AppTemplateConfig appTemplateConfig = new AppTemplateConfig();
        appTemplateConfig.setAppTemplates(buildTemplates(apps, TemplateResourceType.ALL_RESOURCES, orgId, userId, cover));
        return appTemplateConfig;
    }

    private List<com.polaris.lesscode.app.bo.AppTemplate> buildTemplates(List<App> apps, Set<String> resources, Long orgId, Long userId, String cover){
        if (CollectionUtils.isEmpty(apps)){
            return new ArrayList<>();
        }
        List<com.polaris.lesscode.app.bo.AppTemplate> appTemplates = new ArrayList<>();
        List<Long> appIds = apps.stream().map(App::getId).collect(Collectors.toList());
        List<Long> extendIds = apps.stream().filter(a -> Objects.nonNull(a.getExtendsId()) && a.getExtendsId() > 0).map(App::getExtendsId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(extendIds)){
            appIds.addAll(extendIds);
        }

//        List<AppFormResp> formResps = appFormProvider.getFormByAppIds(appIds).getData();
        List<AppView> appViews = appViewMapper.selectList(new LambdaQueryWrapper<AppView>().eq(AppView::getDelFlag, CommonConsts.FALSE).in(AppView::getAppId, appIds).in(AppView::getOwner, 0, -1));
        List<AppPermissionGroupResp> appPermissionGroupRespList = appPermissionApi.getAppPermissionGroupBatch(appIds).getData();
        Map<Long, List<AppView>> viewMap = appViews.stream().collect(Collectors.groupingBy(AppView::getAppId));
//        Map<Long, AppFormResp> formMap = MapUtils.toMap(AppFormResp::getAppId, formResps);
        Map<Long, List<AppPermissionGroupResp>> appPermissionGroupMap = appPermissionGroupRespList.stream().collect(Collectors.groupingBy(AppPermissionGroupResp::getAppId));

        for (App app: apps){
            com.polaris.lesscode.app.bo.AppTemplate appTemplate = new com.polaris.lesscode.app.bo.AppTemplate();
            appTemplate.setId(app.getId());
            appTemplate.setIcon(app.getIcon());
            appTemplate.setParentId(app.getParentId());
            appTemplate.setExtendsId(app.getExtendsId());
            appTemplate.setName(app.getName());
            appTemplate.setType(app.getType());
            appTemplate.setMirrorViewId(app.getMirrorViewId());
            appTemplate.setMirrorAppId(app.getMirrorAppId());
//            AppFormResp appFormResp = formMap.get(app.getId());

            if (resources.contains(TemplateResourceType.RECORDS.getCode())){

//                List<Condition> conditions = new ArrayList<>();
//                conditions.add(Conditions.equal(SqlUtil.wrapperJsonColumn("orgId"), app.getOrgId()));
//                conditions.add(Conditions.equal(SqlUtil.wrapperJsonColumn("appId"), app.getId()));
//                conditions.add(Conditions.equal(SqlUtil.wrapperJsonColumn("recycleFlag"), CommonConsts.FALSE));
//                List<Map<String, Object>> datas = dataCenterApi.query(DataSourceUtil.getDsId(), DataSourceUtil.getDbId(),
//                        Query.select().from(new Table("lc_data")).where(Conditions.and(conditions))).getData();
//                appTemplate.setDataTemplate(datas);
            }

            List<AppView> views = viewMap.get(app.getId());
            if (CollectionUtils.isNotEmpty(views)){
                List<AppViewTemplate> appViewTemplates = new ArrayList<>();
                for (AppView appView: views){
                    AppViewTemplate appViewTemplate = new AppViewTemplate();
                    appViewTemplate.setId(appView.getId());
                    appViewTemplate.setName(appView.getViewName());
                    appViewTemplate.setType(appView.getType());
                    appViewTemplate.setOwner(0L);
                    appViewTemplate.setConfig(appView.getConfig());
                    appViewTemplates.add(appViewTemplate);
                }
                appTemplate.setAppViewTemplates(appViewTemplates);
            }

            // 获取项目模板
            if (Objects.equals(app.getType(), AppType.PROJECT.getCode())){
                ProjectTemplate projectTemplate = projectApi.getProjectTemplateInner(app.getOrgId(), app.getProjectId()).getData();
                log.info("projectTemplate response {}", JsonUtils.toJson(projectTemplate));
                if (projectTemplate != null){
                    if (! resources.contains(TemplateResourceType.ITERATION.getCode())){
                        projectTemplate.setProjectIterationTemplates(null);
                    }
                    TemplateInfo templateInfo = new TemplateInfo();
                    templateInfo.setId(app.getId());
                    templateInfo.setName(app.getName());
                    templateInfo.setIcon(cover);
                    projectTemplate.setTemplateInfo(templateInfo);
                    appTemplate.setAppProjectTemplate(projectTemplate);
                }
            }
            appTemplate.setAppPermissionGroupsTemplates(appPermissionGroupMap.get(app.getId()));
            appTemplates.add(appTemplate);
        }
        return appTemplates;
    }

    private List<App> getSubApps(Long orgId, Long appId){
        List<App> result = new ArrayList<>();
        List<App> apps = appMapper.selectList(new LambdaQueryWrapper<App>().eq(App::getDelFlag, CommonConsts.FALSE).eq(App::getOrgId, orgId));
        if (CollectionUtils.isEmpty(apps)){
            return result;
        }
        Map<Long, App> appMap = MapUtils.toMap(App::getId, apps);
        if (! appMap.containsKey(appId)){
            return result;
        }
        Map<Long, List<App>> appTree = apps.stream().collect(Collectors.groupingBy(App::getParentId));

        Queue<Long> queue = new ArrayDeque<>();
        Set<Long> sets = new HashSet<>();
        queue.add(appId);
        sets.add(appId);
        result.add(appMap.get(appId));
        while(! queue.isEmpty()){
            List<App> subApps = appTree.get(queue.poll());
            if (CollectionUtils.isNotEmpty(subApps)){
                result.addAll(subApps);
                for (App subApp: subApps){
                    if (! sets.contains(subApp.getId())){
                        queue.add(subApp.getId());
                        sets.add(subApp.getId());
                    }
                }
            }
        }
        return result;
    }
}



