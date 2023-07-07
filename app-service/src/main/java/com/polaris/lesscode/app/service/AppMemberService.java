package com.polaris.lesscode.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.polaris.lesscode.app.bo.Member;
import com.polaris.lesscode.app.bo.MemberParser;
import com.polaris.lesscode.app.config.RedisConfig;
import com.polaris.lesscode.app.consts.AppConsts;
import com.polaris.lesscode.app.consts.CacheKeyConsts;
import com.polaris.lesscode.app.consts.PermissionConsts;
import com.polaris.lesscode.app.entity.App;
import com.polaris.lesscode.app.entity.AppRelation;
import com.polaris.lesscode.app.entity.Project;
import com.polaris.lesscode.app.entity.ProjectRelation;
import com.polaris.lesscode.app.internal.enums.AppRelationType;
import com.polaris.lesscode.app.internal.enums.AppType;
import com.polaris.lesscode.app.internal.enums.ProjectRelationType;
import com.polaris.lesscode.app.internal.req.IsProjectMemberBatchReq;
import com.polaris.lesscode.app.mapper.AppMapper;
import com.polaris.lesscode.app.mapper.AppRelationMapper;
import com.polaris.lesscode.app.mapper.ProjectMapper;
import com.polaris.lesscode.app.mapper.ProjectRelationMapper;
import com.polaris.lesscode.app.req.AddAppMemberReq;
import com.polaris.lesscode.app.req.DelAppMemberReq;
import com.polaris.lesscode.app.resp.AppMemberResp;
import com.polaris.lesscode.app.resp.AppResp;
import com.polaris.lesscode.app.vo.ResultCode;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.permission.internal.api.AppPermissionApi;
import com.polaris.lesscode.permission.internal.enums.AppPerDefaultGroupLangCode;
import com.polaris.lesscode.permission.internal.enums.OperateAuthCode;
import com.polaris.lesscode.permission.internal.model.req.AddAppMembersReq;
import com.polaris.lesscode.permission.internal.model.req.RemoveAppMembersReq;
import com.polaris.lesscode.permission.internal.model.resp.AppAuthorityResp;
import com.polaris.lesscode.permission.internal.model.resp.AppPerGroupListItem;
import com.polaris.lesscode.permission.internal.model.resp.MemberGroupMappingsResp;
import com.polaris.lesscode.project.internal.api.ProjectApi;
import com.polaris.lesscode.project.internal.req.ChangeProjectChatMemberReq;
import com.polaris.lesscode.uc.internal.api.UserCenterApi;
import com.polaris.lesscode.uc.internal.req.*;
import com.polaris.lesscode.uc.internal.resp.*;
import com.polaris.lesscode.util.JsonUtils;
import com.polaris.lesscode.util.MapUtils;
import com.polaris.lesscode.util.PlaceholderUtil;
import com.polaris.lesscode.vo.Result;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AppMemberService extends ServiceImpl<AppRelationMapper, AppRelation> {

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private AppRelationMapper appRelationMapper;

    @Autowired
    private ProjectRelationMapper projectRelationMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private RedisConfig redisConfig;

    @Autowired
    private UserCenterApi userCenterApi;

    @Autowired
    private AppPermissionApi appPermissionApi;

    @Autowired
    private AppStarService appStarService;

    @Autowired
    private ProjectApi projectApi;

    @Autowired
    private PermissionService permissionService;

    @Transactional(rollbackFor = Exception.class)
    public List<AppRelation> addMember(Long orgId, Long userId, Long appId, AddAppMemberReq req){
        AppAuthorityResp appAuthorityResp = permissionService.appAuthWithoutCollaborator(orgId, appId, userId);
        if (! appAuthorityResp.hasAppRootPermission() && ! appAuthorityResp.hasAppOptAuth(OperateAuthCode.PERMISSION_PRO_MEMBER_BIND.getCode())){
            throw new BusinessException(ResultCode.FORBIDDEN_ACCESS);
        }

        App app = appMapper.get(orgId, appId);
        if (app == null){
            throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " org:" + orgId + " app:" + appId);
        }
        MemberParser memberParser = MemberParser.parse(req.getMemberIds());
        if (memberParser == null){
            throw new BusinessException(ResultCode.ADD_APP_MEMBERS_ERROR_MEMBER_IDS_FORMAT_ERROR);
        }
        if (Objects.equals(app.getType(), AppType.PROJECT.getCode())){
            List<ProjectRelation> projectRelations = new ArrayList<>();
            Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getAppId, app.getId()).last("limit 1"));
            if (project == null){
                throw new BusinessException(ResultCode.PROJECT_NOT_EXIST);
            }
            for (Long uid: memberParser.getUserIds()){
                projectRelations.add(new ProjectRelation(orgId, project.getId(), uid, 2, userId, userId));
            }
            for (Long deptId: memberParser.getDeptIds()){
                projectRelations.add(new ProjectRelation(orgId, project.getId(), deptId, 25, userId, userId));
            }
            for (ProjectRelation projectRelation: projectRelations){
                appStarService.addRelation(projectRelation);
            }
            projectRelations.removeIf(r -> Objects.isNull(r.getId()));

            ChangeProjectChatMemberReq changeProjectChatMemberReq = new ChangeProjectChatMemberReq();
            changeProjectChatMemberReq.setProjectId(project.getId());
            changeProjectChatMemberReq.setAddUserIds(memberParser.getUserIds());
            changeProjectChatMemberReq.setAddDeptIds(memberParser.getDeptIds());
            projectApi.changeProjectChatMember(orgId, userId, changeProjectChatMemberReq);

            Properties properties = new Properties();
            properties.setProperty("orgId", String.valueOf(orgId));
            properties.setProperty("projectId", String.valueOf(project.getId()));
            redisConfig.delete(PlaceholderUtil.replacePlaceholders(CacheKeyConsts.PROJECT_RELATION_CACHE_KEY, properties));
        }
        List<AppRelation> appRelations = new ArrayList<>();
        for (Long uid: memberParser.getUserIds()){
            appRelations.add(new AppRelation(appId, orgId, AppRelationType.USER.getCode(), uid, userId, userId));
        }
        for (Long deptId: memberParser.getDeptIds()){
            appRelations.add(new AppRelation(appId, orgId, AppRelationType.DEPT.getCode(), deptId, userId, userId));
        }
        for (Long roleId: memberParser.getRoleIds()){
            appRelations.add(new AppRelation(appId, orgId, AppRelationType.ROLE.getCode(), roleId, userId, userId));
        }
        for (AppRelation appRelation: appRelations){
            appStarService.addRelation(appRelation);
        }
        AddAppMembersReq addAppMembersReq = new AddAppMembersReq();
        addAppMembersReq.setOrgId(orgId);
        addAppMembersReq.setAppId(appId);
        addAppMembersReq.setMemberIds(req.getMemberIds());
        addAppMembersReq.setPerGroupId(req.getPerGroupId());
        appPermissionApi.addAppMembers(addAppMembersReq);
        return new ArrayList<>();
    }

    public boolean delMember(Long orgId, Long userId, Long appId, DelAppMemberReq req){
        AppAuthorityResp appAuthorityResp = permissionService.appAuthWithoutCollaborator(orgId, appId, userId);
        if (! appAuthorityResp.hasAppRootPermission() && ! appAuthorityResp.hasAppOptAuth(OperateAuthCode.PERMISSION_PRO_MEMBER_UNBIND.getCode())){
            throw new BusinessException(ResultCode.FORBIDDEN_ACCESS);
        }

        App app = appMapper.get(orgId, appId);
        if (app == null){
            throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " org:" + orgId + " app:" + appId);
        }
        MemberParser memberParser = MemberParser.parse(req.getMemberIds());
        if (memberParser == null){
            throw new BusinessException(ResultCode.ADD_APP_MEMBERS_ERROR_MEMBER_IDS_FORMAT_ERROR);
        }
        if (memberParser.memberSize() == 0){
            throw new BusinessException(ResultCode.DEL_APP_MEMBERS_ERROR_MEMBER_IDS_EMPTY_ERROR);
        }

        if (Objects.equals(app.getType(), AppType.PROJECT.getCode())){
            Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getAppId, app.getId()).last("limit 1"));
            if (project == null){
                throw new BusinessException(ResultCode.PROJECT_NOT_EXIST);
            }
            LambdaUpdateWrapper<ProjectRelation> updateWrapper = new LambdaUpdateWrapper<ProjectRelation>();
            updateWrapper.eq(ProjectRelation::getOrgId, orgId);
            updateWrapper.eq(ProjectRelation::getProjectId, project.getId());
            updateWrapper.set(ProjectRelation::getUpdator, userId);
            updateWrapper.set(ProjectRelation::getIsDelete, CommonConsts.TRUE);
            updateWrapper.and(wrapper -> {
                if (CollectionUtils.isNotEmpty(memberParser.getUserIds())){
                    wrapper.or().eq(ProjectRelation::getRelationType, 2).in(ProjectRelation::getRelationId, memberParser.getUserIds());
                    wrapper.or().eq(ProjectRelation::getRelationType, 1).in(ProjectRelation::getRelationId, memberParser.getUserIds());
                }
                if (CollectionUtils.isNotEmpty(memberParser.getDeptIds())){
                    wrapper.or().eq(ProjectRelation::getRelationType, 25).in(ProjectRelation::getRelationId, memberParser.getDeptIds());
                }
                return wrapper;
            });
            projectRelationMapper.update(null, updateWrapper);
            ChangeProjectChatMemberReq changeProjectChatMemberReq = new ChangeProjectChatMemberReq();
            changeProjectChatMemberReq.setProjectId(project.getId());
            changeProjectChatMemberReq.setDelUserIds(memberParser.getUserIds());
            changeProjectChatMemberReq.setDelDeptIds(memberParser.getDeptIds());

            if (CollectionUtils.isNotEmpty(memberParser.getUserIds()) && memberParser.getUserIds().contains(project.getOwner())){
                Project projectUpdate = new Project();
                projectUpdate.setId(project.getId());
                projectUpdate.setOwner(0L);
                projectMapper.updateById(projectUpdate);
            }

            Properties properties = new Properties();
            properties.setProperty("orgId", String.valueOf(orgId));
            properties.setProperty("projectId", String.valueOf(project.getId()));
            redisConfig.delete(PlaceholderUtil.replacePlaceholders(CacheKeyConsts.PROJECT_RELATION_CACHE_KEY, properties));
            projectApi.changeProjectChatMember(orgId, userId, changeProjectChatMemberReq);
        }
        LambdaUpdateWrapper<AppRelation> updateWrapper = new LambdaUpdateWrapper<AppRelation>();
        updateWrapper.eq(AppRelation::getOrgId, orgId);
        updateWrapper.eq(AppRelation::getAppId, appId);
        updateWrapper.set(AppRelation::getUpdator, userId);
        updateWrapper.set(AppRelation::getDelFlag, CommonConsts.TRUE);
        updateWrapper.and(wrapper -> {
            if (CollectionUtils.isNotEmpty(memberParser.getUserIds())){
                wrapper.or().eq(AppRelation::getType, AppRelationType.USER.getCode()).in(AppRelation::getRelationId, memberParser.getUserIds());
            }
            if (CollectionUtils.isNotEmpty(memberParser.getDeptIds())){
                wrapper.or().eq(AppRelation::getType, AppRelationType.DEPT.getCode()).in(AppRelation::getRelationId, memberParser.getDeptIds());
            }
            if (CollectionUtils.isNotEmpty(memberParser.getRoleIds())){
                wrapper.or().eq(AppRelation::getType, AppRelationType.ROLE.getCode()).in(AppRelation::getRelationId, memberParser.getRoleIds());
            }
            return wrapper;
        });
        update(updateWrapper);
        RemoveAppMembersReq removeAppMembersReq = new RemoveAppMembersReq();
        removeAppMembersReq.setOrgId(orgId);
        removeAppMembersReq.setAppId(appId);
        removeAppMembersReq.setMemberIds(req.getMemberIds());
        appPermissionApi.removeAppMembers(removeAppMembersReq);
        return true;
    }

    public boolean isProjectMember(Long orgId, Long appId, Long userId){
        App app = appMapper.get(appId);
        if (app == null){
            return false;
        }
        UserAuthorityReq userAuthorityReq = new UserAuthorityReq();
        userAuthorityReq.setOrgId(orgId);
        userAuthorityReq.setUserId(userId);
        UserAuthorityResp userAuthorityResp = userCenterApi.getUserAuthority(userAuthorityReq).getData();
        if (Objects.isNull(userAuthorityResp.getRefDeptIds())){
            userAuthorityResp.setRefDeptIds(new ArrayList<>());
        }
        userAuthorityResp.getRefDeptIds().add(0L);
        List<AppRelation> relations = appRelationMapper.selectList(new LambdaQueryWrapper<AppRelation>()
                .eq(AppRelation::getDelFlag, CommonConsts.FALSE)
                .eq(AppRelation::getOrgId, orgId)
                .eq(AppRelation::getAppId, appId)
                .and(wrapper -> {
                    wrapper.or().eq(AppRelation::getType, AppRelationType.USER.getCode()).eq(AppRelation::getRelationId, userId);
                     wrapper.or().eq(AppRelation::getType, AppRelationType.DEPT.getCode()).in(AppRelation::getRelationId, userAuthorityResp.getRefDeptIds());
                    if (CollectionUtils.isNotEmpty(userAuthorityResp.getRefRoleIds())){
                        wrapper.or().eq(AppRelation::getType, AppRelationType.ROLE.getCode()).in(AppRelation::getRelationId, userAuthorityResp.getRefRoleIds());
                    }
                    return wrapper;
                }));
        if (! relations.isEmpty()){
            return true;
        }

        if (Objects.equals(app.getType(), AppType.PROJECT.getCode())){
            LambdaQueryWrapper<ProjectRelation> projectRelationLambdaQueryWrapper = new LambdaQueryWrapper<>();
            projectRelationLambdaQueryWrapper.eq(ProjectRelation::getIsDelete, CommonConsts.FALSE);
            projectRelationLambdaQueryWrapper.eq(ProjectRelation::getOrgId, orgId);
            projectRelationLambdaQueryWrapper.eq(ProjectRelation::getProjectId, app.getProjectId());
            projectRelationLambdaQueryWrapper.and(wrapper -> {
                wrapper.or().eq(ProjectRelation::getRelationType, ProjectRelationType.OWNER.getCode()).eq(ProjectRelation::getRelationId, userId);
                wrapper.or().eq(ProjectRelation::getRelationType, ProjectRelationType.PARTICIPANT.getCode()).eq(ProjectRelation::getRelationId, userId);
//                wrapper.or().eq(ProjectRelation::getRelationType, ProjectRelationType.FOLLOWER.getCode()).eq(ProjectRelation::getRelationId, userId);
                wrapper.or().eq(ProjectRelation::getRelationType, ProjectRelationType.DEPT.getCode()).in(ProjectRelation::getRelationId, userAuthorityResp.getRefDeptIds());
                return wrapper;
            });
            List<ProjectRelation> projectRelations = projectRelationMapper.selectList(projectRelationLambdaQueryWrapper);
            return ! projectRelations.isEmpty();
        }
        return false;
    }

   public Map<Long, Boolean> isProjectMemberBatch(IsProjectMemberBatchReq req){
       Map<Long, Boolean> results = new HashMap<>();
       if (Objects.isNull(req.getRefDeptIds())){
           req.setRefDeptIds(new ArrayList<>());
       }
       req.getRefDeptIds().add(0L);
       List<AppRelation> relations = appRelationMapper.selectList(new LambdaQueryWrapper<AppRelation>()
               .eq(AppRelation::getDelFlag, CommonConsts.FALSE)
               .eq(AppRelation::getOrgId, req.getOrgId())
               .in(AppRelation::getAppId, req.getAppIds())
               .and(wrapper -> {
                   wrapper.or().eq(AppRelation::getType, AppRelationType.USER.getCode()).eq(AppRelation::getRelationId, req.getUserId());
                   wrapper.or().eq(AppRelation::getType, AppRelationType.DEPT.getCode()).in(AppRelation::getRelationId, req.getRefDeptIds());
                   if (CollectionUtils.isNotEmpty(req.getRefDeptIds())){
                       wrapper.or().eq(AppRelation::getType, AppRelationType.ROLE.getCode()).in(AppRelation::getRelationId, req.getRefRoleIds());
                   }
                   return wrapper;
               }));
       if (CollectionUtils.isNotEmpty(relations)){
           for (AppRelation relation: relations){
               results.put(relation.getAppId(), true);
           }
       }

       if (! MapUtils.isEmpty(req.getAppProjectIds())){
           LambdaQueryWrapper<ProjectRelation> projectRelationLambdaQueryWrapper = new LambdaQueryWrapper<>();
           projectRelationLambdaQueryWrapper.eq(ProjectRelation::getIsDelete, CommonConsts.FALSE);
           projectRelationLambdaQueryWrapper.eq(ProjectRelation::getOrgId, req.getOrgId());
           projectRelationLambdaQueryWrapper.in(ProjectRelation::getProjectId, req.getAppProjectIds().keySet());
           projectRelationLambdaQueryWrapper.and(wrapper -> {
               wrapper.or().eq(ProjectRelation::getRelationType, ProjectRelationType.OWNER.getCode()).eq(ProjectRelation::getRelationId, req.getUserId());
               wrapper.or().eq(ProjectRelation::getRelationType, ProjectRelationType.PARTICIPANT.getCode()).eq(ProjectRelation::getRelationId, req.getUserId());
//               wrapper.or().eq(ProjectRelation::getRelationType, ProjectRelationType.FOLLOWER.getCode()).eq(ProjectRelation::getRelationId, req.getUserId());
               wrapper.or().eq(ProjectRelation::getRelationType, ProjectRelationType.DEPT.getCode()).in(ProjectRelation::getRelationId, req.getRefDeptIds());
               return wrapper;
           });
           List<ProjectRelation> projectRelations = projectRelationMapper.selectList(projectRelationLambdaQueryWrapper);
           if (CollectionUtils.isNotEmpty(projectRelations)){
               for (ProjectRelation projectRelation: projectRelations){
                   results.put(req.getAppProjectIds().get(projectRelation.getProjectId()), true);
               }
           }
       }
       return results;
    }

    /**
     * 获取应用的成员信息
     **/
    public List<AppMemberResp> getMembers(Long orgId, Long userId, Long appId, boolean allUser){
        // 判断是否继承上级，如果是，则取上级权限
        App app = appMapper.get(orgId, appId);
        if (app == null){
            throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " org:" + orgId + " app:" + appId);
        }
        boolean isExtends = ! Objects.equals(app.getId(), appId);
        App targetApp = Objects.equals(app.getAuthType(), 1) ? getExtendsAuthApp(app) : app;
        targetApp.setCreator(app.getCreator()); //创建人权限传递
        List<Member> members = getAppMembers(orgId, userId, targetApp, allUser,  Objects.equals(app.getAuthType(), 1));
        List<AppMemberResp> results = new ArrayList<>();
        for (Member member: members){
            results.add(new AppMemberResp(member));
        }
        results.sort(AppMemberResp::compareTo);
        results = distinctMembers(results, isExtends);
        return results;
    }

    private List<AppMemberResp> distinctMembers(List<AppMemberResp> results, boolean isExtends){
        List<AppMemberResp> newResults = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(results)){
            Set<String> dp = new HashSet<>();
            for (AppMemberResp member: results){
                String key = member.getType() + ":" + member.getId();
                if (! dp.contains(key)){
                    // 继承情况下，去掉不带自定义权限的成员
                    if (isExtends && CollectionUtils.isNotEmpty(member.getPerGroups())){
                        boolean hasDefaultPer = false;
                        for (AppPerGroupListItem item: member.getPerGroups()){
                            if (Objects.nonNull(item.getLangCode()) && AppPerDefaultGroupLangCode.forValue(item.getLangCode()) != null){
                                hasDefaultPer = true;
                                break;
                            }
                        }
                        if (!hasDefaultPer){
                            continue;
                        }
                    }
                    newResults.add(member);
                    dp.add(key);
                }
            }
        }
        return newResults;
    }

    private List<Member> getAppMembers(Long orgId, Long userId, App app, boolean allUser, boolean isExtendsAuth) {
        //获取管理组成员
        GetManagerReq getManagerReq = new GetManagerReq();
        getManagerReq.setOrgId(app.getOrgId());
        List<GetManagerData> managers = userCenterApi.getManager(getManagerReq).getData().getData();
        Map<Long, GetManagerData> userManagerData = new HashMap<>();
        Map<Long, GetManagerData> deptManagerData = new HashMap<>();
        Map<Long, GetManagerData> roleManagerData = new HashMap<>();
        if (CollectionUtils.isNotEmpty(managers)) {
            managers.removeIf(m -> {
                if (Objects.equals(m.getLangCode(), AppConsts.MANAGE_GROUP_SYS)) {
                    return false;
                }
                if (Objects.equals(m.getLangCode(), AppConsts.MANAGE_GROUP_SUB_NORMALADMIN) || (Objects.equals(m.getLangCode(), AppConsts.MANAGE_GROUP_SUB))) {
                    if (CollectionUtils.isNotEmpty(m.getAppIds())) {
                        return !m.getAppIds().contains(app.getId()) && !m.getAppIds().contains(-1L);
                    }
                }
                return true;
            });
            for (GetManagerData manager: managers) {
                if (Objects.equals(AppConsts.MEMBER_USER_TYPE, manager.getMemberType())) {
                    userManagerData.put(manager.getMemberId(), manager);
                } else if (Objects.equals(AppConsts.MEMBER_DEPT_TYPE, manager.getMemberType())) {
                    deptManagerData.put(manager.getMemberId(), manager);
                } else if (Objects.equals(AppConsts.MEMBER_ROLE_TYPE, manager.getMemberType())) {
                    roleManagerData.put(manager.getMemberId(), manager);
                }
            }
        }

        List<AppPerGroupListItem> appGroupList = appPermissionApi.getAppPermissionGroupList(orgId, app.getId()).getData();
        Map<String, AppPerGroupListItem> appGroupMap = new HashMap<>();
        for (AppPerGroupListItem appGroup: appGroupList) {
            if (StringUtils.isNotBlank(appGroup.getLangCode())) {
                appGroupMap.put(appGroup.getLangCode(), appGroup);
            }
        }
        AppPerGroupListItem adminPerGroup = appGroupMap.get(Objects.equals(app.getType(), AppType.PROJECT.getCode()) ? AppPerDefaultGroupLangCode.OWNER.getCode() : AppPerDefaultGroupLangCode.FORM_ADMINISTRATOR.getCode());
        AppPerGroupListItem readPerGroup = appGroupMap.get(Objects.equals(app.getType(), AppType.PROJECT.getCode()) ? AppPerDefaultGroupLangCode.PROJECT_MEMBER.getCode() : AppPerDefaultGroupLangCode.READ.getCode());
        if (adminPerGroup == null) {
            adminPerGroup = PermissionConsts.OWNER_PER_GROUP;
        }
        if (readPerGroup == null) {
            readPerGroup = PermissionConsts.PROJECT_MEMBER_PER_GROUP;
        }

        List<Member> members = new ArrayList<>();
        GetOrgInfoResp orgInfoResp = userCenterApi.getOrgInfo(orgId).getData();
        //企业负责人也加进来
        members.add(new Member(orgInfoResp.getOrgOwnerId(), "", null, AppConsts.MEMBER_USER_TYPE, 1, 2, true, false, Objects.equals(app.getCreator(), orgInfoResp.getOrgOwnerId()), Collections.singletonList(adminPerGroup)));

//        boolean isContainsCurrentUser = false;
//        for (GetManagerData manager: managers){
//            if (Objects.equals(manager.getMemberType(), AppConsts.MEMBER_USER_TYPE) && Objects.equals(manager.getMemberId(), app.getCreator())){
//                isContainsCurrentUser = true;
//                break;
//            }
//        }
//        if (! isContainsCurrentUser){
//            members.add(new Member(app.getCreator(), "", null, AppConsts.MEMBER_USER_TYPE, 1, 2, false, false, true, Collections.singletonList(adminPerGroup)));
//        }
        if (CollectionUtils.isNotEmpty(managers)) {
            for (GetManagerData manager: managers) {
                boolean isSysAdmin = Objects.equals(manager.getLangCode(), AppConsts.MANAGE_GROUP_SYS);
                boolean isSubAdmin = Objects.equals(manager.getLangCode(), AppConsts.MANAGE_GROUP_SUB) || Objects.equals(manager.getLangCode(), AppConsts.MANAGE_GROUP_SUB_NORMALADMIN);
                boolean isOwner = false;
                if (Objects.equals(manager.getMemberType(), AppConsts.MEMBER_USER_TYPE)) {
                    isOwner = Objects.equals(app.getCreator(), manager.getMemberId());
                }
                members.add(new Member(manager.getMemberId(), "", null, manager.getMemberType().toUpperCase(Locale.ROOT), 1, 2, isSysAdmin, isSubAdmin, isOwner, Collections.singletonList(adminPerGroup)));
            }
        }

        List<AppRelation> memberRelations = getMemberRelations(app);
        if (CollectionUtils.isNotEmpty(memberRelations)) {
            for (AppRelation relation: memberRelations) {
                String memberType = AppConsts.MEMBER_USER_TYPE;
                GetManagerData manager = userManagerData.get(relation.getRelationId());
                if (Objects.equals(relation.getType(), AppRelationType.DEPT.getCode())) {
                    memberType = AppConsts.MEMBER_DEPT_TYPE;
                    manager = deptManagerData.get(relation.getRelationId());
                } else if (Objects.equals(relation.getType(), AppRelationType.ROLE.getCode())) {
                    memberType = AppConsts.MEMBER_ROLE_TYPE;
                    manager = roleManagerData.get(relation.getRelationId());
                }
                boolean isSysAdmin = false, isSubAdmin = false, isOwner = false;
                if (manager != null) {
                    isSysAdmin = Objects.equals(manager.getLangCode(), AppConsts.MANAGE_GROUP_SYS);
                    isSubAdmin = Objects.equals(manager.getLangCode(), AppConsts.MANAGE_GROUP_SUB) || Objects.equals(manager.getLangCode(), AppConsts.MANAGE_GROUP_SUB_NORMALADMIN);
                    isOwner = false;
                    if (Objects.equals(relation.getType(), AppRelationType.USER.getCode())) {
                        isOwner = Objects.equals(app.getCreator(), manager.getMemberId());
                    }
                }
                Member m = new Member(relation.getRelationId(), "", null, memberType, 1, 2, isSysAdmin, isSubAdmin, isOwner);
                if (Objects.equals(relation.getType(), AppRelationType.DEPT.getCode()) && Objects.equals(relation.getRelationId(), 0L)) {
                    m.setName("全部成员");
                }
                members.add(m);
            }
        }

        GetMemberSimpleInfoReq getMemberSimpleInfoReq = new GetMemberSimpleInfoReq();
        getMemberSimpleInfoReq.setOrgId(app.getOrgId());
        getMemberSimpleInfoReq.setType(1);
        List<MemberSimpleInfo> userInfos = userCenterApi.getMemberSimpleInfo(getMemberSimpleInfoReq).getData().getData();
        getMemberSimpleInfoReq.setType(2);
        List<MemberSimpleInfo> deptInfos = userCenterApi.getMemberSimpleInfo(getMemberSimpleInfoReq).getData().getData();
        getMemberSimpleInfoReq.setType(3);
        List<MemberSimpleInfo> roleInfos = userCenterApi.getMemberSimpleInfo(getMemberSimpleInfoReq).getData().getData();

        MemberGroupMappingsResp memberGroupMappingsResp = appPermissionApi.memberGroupMappings(orgId, app.getId(), userId).getData();
        Map<Long, List<AppPerGroupListItem>> userGroupMappings = memberGroupMappingsResp.getUserGroupMappings() == null ? new HashMap<>() : memberGroupMappingsResp.getUserGroupMappings();
        Map<Long, List<AppPerGroupListItem>> deptGroupMappings = memberGroupMappingsResp.getDeptGroupMappings() == null ? new HashMap<>() : memberGroupMappingsResp.getDeptGroupMappings();
        Map<Long, List<AppPerGroupListItem>> roleGroupMappings = memberGroupMappingsResp.getRoleGroupMappings() == null ? new HashMap<>() : memberGroupMappingsResp.getRoleGroupMappings();

        for (Member member: members) {
            if (Objects.equals(app.getCreator(), member.getId())) {
                member.setOwner(true);
            }
            if (CollectionUtils.isNotEmpty(member.getPerGroups())) {
                continue;
            }
            if (Objects.equals(member.getType(), AppConsts.MEMBER_USER_TYPE)) {
                member.setPerGroups(userGroupMappings.get(member.getId()));
            } else if (Objects.equals(member.getType(), AppConsts.MEMBER_DEPT_TYPE)) {
                member.setPerGroups(deptGroupMappings.get(member.getId()));
            } else if (Objects.equals(member.getType(), AppConsts.MEMBER_ROLE_TYPE)){
                member.setPerGroups(roleGroupMappings.get(member.getId()));
            }
        }
        // 补默认角色
        for (Member member: members) {
            if (CollectionUtils.isEmpty(member.getPerGroups())) {
                member.setPerGroups(Collections.singletonList(readPerGroup));
            }
        }
        // 判断是否只需要用户
        if (allUser) {
            members = unfoldMembers(members, userManagerData, userInfos, deptInfos, roleInfos, app);
        }
        assemblyMembers(members, deptInfos, roleInfos, app);
        return members;
    }

    private List<Member> unfoldMembers(List<Member> members, Map<Long, GetManagerData> userManagerData, List<MemberSimpleInfo> userInfos, List<MemberSimpleInfo> deptInfos, List<MemberSimpleInfo> roleInfos, App app){
        GetDeptUserIdsReq getDeptUserIdsReq = new GetDeptUserIdsReq();
        getDeptUserIdsReq.setOrgId(app.getOrgId());
        Map<Long, List<Long>> deptUserIds = userCenterApi.getDeptUserIds(getDeptUserIdsReq).getData().getData();
        GetRoleUserIdsReq getRoleUserIdsReq = new GetRoleUserIdsReq();
        getRoleUserIdsReq.setOrgId(app.getOrgId());
        Map<Long, List<Long>> roleUserIds = userCenterApi.getRoleUserIds(getRoleUserIdsReq).getData().getData();
        GetMemberSimpleInfoReq getMemberSimpleInfoReq = new GetMemberSimpleInfoReq();
        getMemberSimpleInfoReq.setOrgId(app.getOrgId());
        getMemberSimpleInfoReq.setType(2);
        Map<Long, List<Long>> deptParentMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(members)){
            for (MemberSimpleInfo deptInfo: deptInfos){
                List<Long> subDeptIds = deptParentMap.computeIfAbsent(deptInfo.getParentId(), k -> new ArrayList<>());
                subDeptIds.add(deptInfo.getId());
            }
        }
        Map<Long, MemberSimpleInfo> users = MapUtils.toMap(MemberSimpleInfo::getId, userInfos);

        List<Member> newMembers = new ArrayList<>();
        Map<Long, List<AppPerGroupListItem>> userPerGroupMap = new HashMap<>();
        Set<Long> alreadyUserIds = members.stream().filter(member -> Objects.equals(member.getType(), AppConsts.MEMBER_USER_TYPE)).map(Member::getId).collect(Collectors.toSet());
        for (Member member: members){
            Set<Long> userIds = new HashSet<>();
            if (Objects.equals(member.getType(), AppConsts.MEMBER_DEPT_TYPE)){
                if (Objects.equals(member.getId(), 0L)){     // 所有成员
                    userIds.addAll(users.keySet());
                }else{
                    // 获取部门下所有的用户（包括子部门）
                    List<Long> deptIds = currentAndChildDeptIds(member.getId(), deptParentMap);
                    if (CollectionUtils.isNotEmpty(deptIds) && ! MapUtils.isEmpty(deptUserIds)){
                        for (Long deptId: deptIds){
                            List<Long> uids = deptUserIds.get(deptId);
                            if (CollectionUtils.isNotEmpty(uids)){
                                userIds.addAll(uids);
                            }
                        }
                    }
                }
            }else if (Objects.equals(member.getType(), AppConsts.MEMBER_ROLE_TYPE)){
                if (! MapUtils.isEmpty(roleUserIds) && CollectionUtils.isNotEmpty(roleUserIds.get(member.getId()))){
                    userIds.addAll(roleUserIds.get(member.getId()));
                }
            }else{
                newMembers.add(member);
            }
            if (CollectionUtils.isNotEmpty(userIds)){
                for (Long userId: userIds){
                    List<AppPerGroupListItem> perGroups = userPerGroupMap.computeIfAbsent(userId, k -> new ArrayList<>());
                    if (CollectionUtils.isNotEmpty(member.getPerGroups())){
                        perGroups.addAll(member.getPerGroups());
                    }
                    if (alreadyUserIds.contains(userId)){
                        continue;
                    }
                    alreadyUserIds.add(userId);
                    MemberSimpleInfo userInfo = users.get(userId);
                    if (userInfo != null){
                        GetManagerData manager = userManagerData.get(userInfo.getId());
                        boolean isSysAdmin = false, isSubAdmin = false, isOwner = false;
                        if (manager != null){
                            isSysAdmin = Objects.equals(manager.getLangCode(), AppConsts.MANAGE_GROUP_SYS);
                            isSubAdmin = Objects.equals(manager.getLangCode(), AppConsts.MANAGE_GROUP_SUB) || Objects.equals(manager.getLangCode(), AppConsts.MANAGE_GROUP_SUB_NORMALADMIN);
                            isOwner = Objects.equals(app.getCreator(), userId);
                        }
                        newMembers.add(new Member(userInfo.getId(), userInfo.getName(), null, AppConsts.MEMBER_USER_TYPE, 1, 2, isSysAdmin, isSubAdmin, isOwner, member.getPerGroups()));
                    }
                }
            }
        }
        for (Member member: members){
            if (Objects.equals(member.getType(), AppConsts.MEMBER_USER_TYPE)){
                List<AppPerGroupListItem> perGroups = userPerGroupMap.get(member.getId());
                if (CollectionUtils.isNotEmpty(perGroups)){
                    if (CollectionUtils.isNotEmpty(member.getPerGroups())){
                        perGroups.addAll(member.getPerGroups());
                    }

                    List<AppPerGroupListItem> filter = new ArrayList<>();
                    Set<Long> perIdSet = new HashSet<>();
                    for (AppPerGroupListItem per: perGroups){
                        if (perIdSet.contains(per.getId())){
                            continue;
                        }
                        perIdSet.add(per.getId());
                        filter.add(per);
                    }
                    member.setPerGroups(filter);
                }
            }
        }
        return newMembers;
    }

    private List<Long> currentAndChildDeptIds(Long deptId, Map<Long, List<Long>> deptParentMap){
        Set<Long> repeat = new HashSet<>();
        Queue<Long> parentIds = new LinkedBlockingQueue<>();
        repeat.add(deptId);
        parentIds.add(deptId);
        while(true){
            Long parentId = parentIds.poll();
            if (parentId == null){
                break;
            }
            List<Long> childs = deptParentMap.get(parentId);
            if (CollectionUtils.isNotEmpty(childs)){
                childs.removeIf(repeat::contains);
                parentIds.addAll(childs);
                repeat.addAll(childs);
            }
        }
        return new ArrayList<>(repeat);
    }

    /**
     * 封装成员更多信息
     *
     * @Author Nico
     * @Date 2021/6/2 14:04
     **/
    private void assemblyMembers(List<Member> members, List<MemberSimpleInfo> deptInfos, List<MemberSimpleInfo> roleInfos, App app){
        List<Long> userIds = new ArrayList<>();
        Map<Long, UserInfoResp> userMap = new HashMap<>();
        Map<Long, MemberSimpleInfo> deptMap = MapUtils.toMap(MemberSimpleInfo::getId, deptInfos);
        Map<Long, MemberSimpleInfo> roleMap = MapUtils.toMap(MemberSimpleInfo::getId, roleInfos);
        if (CollectionUtils.isNotEmpty(members)){
            for (Member member: members){
                if (Objects.equals(member.getType(), AppConsts.MEMBER_USER_TYPE)){
                    userIds.add(member.getId());
                }
            }
        }
        UserListByIdsReq userListByIdsReq = new UserListByIdsReq();
        userListByIdsReq.setOrgId(app.getOrgId());
        userListByIdsReq.setIds(userIds);
        List<UserInfoResp> userInfoRespList = userCenterApi.getAllUserListByIds(userListByIdsReq).getData();
        if (CollectionUtils.isNotEmpty(userInfoRespList)){
            userMap = MapUtils.toMap(UserInfoResp::getId, userInfoRespList);
        }

        Map<Long, Long> deptParentId = new HashMap<>();
        for (MemberSimpleInfo memberSimpleInfo: deptInfos){
            deptParentId.put(memberSimpleInfo.getId(), memberSimpleInfo.getParentId());
        }

        if (CollectionUtils.isNotEmpty(members)){
            for (Member member: members){
                if (Objects.equals(member.getType(), AppConsts.MEMBER_USER_TYPE)){
                    UserInfoResp userInfoResp = userMap.get(member.getId());
                    if (userInfoResp != null){
                        List<List<MemberSimpleInfo>> departments = new ArrayList<>();
                        List<MemberSimpleInfo> roles = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(userInfoResp.getDeptList())){
                            for (DeptBindData dept: userInfoResp.getDeptList()){
                                departments.add(getLinkedDepts(dept.getDepartmentId(), deptMap, deptParentId));
                            }
                        }
                        if (CollectionUtils.isNotEmpty(userInfoResp.getRoleList())){
                            for (RoleBindData role: userInfoResp.getRoleList()){
                                roles.add(roleMap.get(role.getRoleId()));
                            }
                        }
                        member.setName(userInfoResp.getName());
                        member.setStatus(userInfoResp.getStatus());
                        member.setIsDelete(userInfoResp.getIsDelete());
                        member.setAvatar(userInfoResp.getAvatar());
                        member.setRoles(roles);
                        member.setDepartments(departments);
                    }
                }else if (Objects.equals(member.getType(), AppConsts.MEMBER_DEPT_TYPE)){
                    MemberSimpleInfo deptInfoResp = deptMap.get(member.getId());
                    if (deptInfoResp != null){
                        member.setName(deptInfoResp.getName());
                    }
                }else if (Objects.equals(member.getType(), AppConsts.MEMBER_ROLE_TYPE)){
                    MemberSimpleInfo roleInfoResp = roleMap.get(member.getId());
                    if (roleInfoResp != null){
                        member.setName(roleInfoResp.getName());
                    }
                }
            }
        }
    }

    private List<MemberSimpleInfo> getLinkedDepts(Long deptId, Map<Long, MemberSimpleInfo> deptMap, Map<Long, Long> deptParentId){
        List<MemberSimpleInfo> members = new ArrayList<>();
        if (deptMap.get(deptId) != null){
            members.add(deptMap.get(deptId));
        }
        while(true){
            MemberSimpleInfo dept = deptMap.get(deptParentId.get(deptId));
            if (dept != null){
                members.add(dept);
                deptId = dept.getParentId();
            }else{
                break;
            }
        }
        Collections.reverse(members);
        return members;
    }

    public App getExtendsAuthApp(App app){
        if (Objects.equals(app.getParentId(),  0L)){
            return app;
        }
        if (! Objects.equals(app.getAuthType(), 1)) {
            return app;
        }
        App parent = appMapper.get(app.getOrgId(), app.getParentId());
        if (parent == null){
            return app;
        }
        return getExtendsAuthApp(parent);
    }

//    public void copyAppRelationMember(Long sourceAppId, Long targetAppId){
//        List<AppRelation> relations = getMemberRelations(sourceAppId);
//        if (CollectionUtils.isNotEmpty(relations)){
//            for (AppRelation relation: relations){
//                relation.setId(null);
//                relation.setAppId(targetAppId);
//            }
//            saveBatch(relations);
//        }
//    }

    private List<AppRelation> getMemberRelations(App app){
        if (Objects.equals(app.getType(), AppType.PROJECT.getCode())){
            Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getAppId, app.getId()).last("limit 1"));
            if (project == null){
                throw new BusinessException(ResultCode.PROJECT_NOT_EXIST);
            }
            List<ProjectRelation> projectRelations = projectRelationMapper.selectList(new LambdaQueryWrapper<ProjectRelation>()
                    .eq(ProjectRelation::getIsDelete, CommonConsts.FALSE)
                    .eq(ProjectRelation::getOrgId, app.getOrgId())
                    .eq(ProjectRelation::getProjectId, project.getId())
                    .in(ProjectRelation::getRelationType, Arrays.asList(
                            ProjectRelationType.OWNER.getCode(),
                            ProjectRelationType.PARTICIPANT.getCode(),
                            ProjectRelationType.DEPT.getCode()))
            );
            List<AppRelation> appRelations = new ArrayList<>();
            for (ProjectRelation projectRelation: projectRelations){
                AppRelation appRelation = new AppRelation();
                appRelation.setOrgId(app.getOrgId());
                appRelation.setAppId(app.getId());
                if (Objects.equals(projectRelation.getRelationType(), ProjectRelationType.DEPT.getCode())){
                    appRelation.setType(AppRelationType.DEPT.getCode());
                }else{
                    appRelation.setType(AppRelationType.USER.getCode());
                }
                appRelation.setRelationId(projectRelation.getRelationId());
                appRelations.add(appRelation);
            }
            return appRelations;
        }else{
            return appRelationMapper.selectList(new LambdaQueryWrapper<AppRelation>()
                    .eq(AppRelation::getDelFlag, CommonConsts.FALSE)
                    .eq(AppRelation::getAppId, app.getId())
                    .in(AppRelation::getType, Arrays.asList(AppRelationType.USER.getCode(), AppRelationType.DEPT.getCode(), AppRelationType.ROLE.getCode())));
        }
    }


    public boolean addAppMember(Long appId, Integer relationType, Long relationId){
        App app = appMapper.get(appId);
        if (app == null){
            throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " app:" + appId);
        }
        AppRelation appRelation = new AppRelation();
        appRelation.setAppId(appId);
        appRelation.setRelationId(relationId);
        appRelation.setType(relationType);
        appRelation.setOrgId(app.getOrgId());
        appRelation.setCreator(app.getCreator());
        appRelation.setUpdator(app.getUpdator());
        appStarService.addRelation(appRelation);
        return true;
    }

}
