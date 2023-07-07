package com.polaris.lesscode.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.polaris.lesscode.app.bo.Member;
import com.polaris.lesscode.app.consts.AppConsts;
import com.polaris.lesscode.app.entity.App;
import com.polaris.lesscode.app.internal.service.AppCollaboratorInternalService;
import com.polaris.lesscode.app.resp.AppMemberResp;
import com.polaris.lesscode.app.resp.MemberResp;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.form.internal.api.AppFormApi;
import com.polaris.lesscode.form.internal.api.AppValueApi;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.req.AppValueListReq;
import com.polaris.lesscode.form.internal.resp.AppFormFilter;
import com.polaris.lesscode.form.internal.resp.AppFormResp;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.gotable.internal.resp.TableSchemas;
import com.polaris.lesscode.permission.internal.api.AppPermissionApi;
import com.polaris.lesscode.permission.internal.api.PermissionApi;
import com.polaris.lesscode.permission.internal.enums.AppPerDefaultGroupLangCode;
import com.polaris.lesscode.permission.internal.model.resp.AppPerGroupListItem;
import com.polaris.lesscode.uc.internal.api.UserCenterApi;
import com.polaris.lesscode.uc.internal.req.GetDeptUserIdsReq;
import com.polaris.lesscode.uc.internal.req.GetMemberSimpleInfoReq;
import com.polaris.lesscode.uc.internal.req.UserListByIdsReq;
import com.polaris.lesscode.uc.internal.resp.DeptBindData;
import com.polaris.lesscode.uc.internal.resp.MemberSimpleInfo;
import com.polaris.lesscode.uc.internal.resp.RoleBindData;
import com.polaris.lesscode.uc.internal.resp.UserInfoResp;
import com.polaris.lesscode.vo.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppCollaboratorService {

    @Autowired
    private GoTableService goTableService;

    @Autowired
    private AppValueApi appValueApi;

    @Autowired
    private UserCenterApi userCenterApi;

    @Autowired
    private AppPermissionApi appPermissionApi;

    @Autowired
    private AppMemberService appMemberService;

    @Autowired
    private AppCollaboratorInternalService appCollaboratorInternalService;

    /**
     * 获取应用的成员信息
     **/
    public List<AppMemberResp> getCollaborators(Long orgId, Long userId, Long appId,  Long tableId, boolean allUser){
        List<AppMemberResp> resps = new ArrayList<>();
        //查看表头配置中作为协作人的字段
        TableSchemas tableSchemas = goTableService.readSchemaByAppId(appId, orgId, tableId, userId);
        Map<String, List<Long>> allFieldMap = new LinkedHashMap<>();
        for (JSONObject obj : tableSchemas.getColumns()) {
            FieldParam field = JSON.toJavaObject(obj,FieldParam.class);
            if (appCollaboratorInternalService.judgeHasCollaboratorRoles(field)) {
                Object collaboratorRolesObj = field.getField().getProps().get("collaboratorRoles");
                allFieldMap.put(field.getName(), appCollaboratorInternalService.objToList(collaboratorRolesObj));
            }
        }
        if (MapUtils.isEmpty(allFieldMap)) {
            return resps;
        }
        
        //查询数据并组装
        AppValueListReq appValueListReq = new AppValueListReq();
        appValueListReq.setPage(0);
        appValueListReq.setSize(0);
        appValueListReq.setOrgId(orgId);
        List<Long> appIds = new ArrayList<>();
        appIds.add(appId);
        appValueListReq.setRedirectIds(appIds);
        Page<Map<String, Object>> datas =  appValueApi.filter(appId, appValueListReq).getData();
        if (CollectionUtils.isEmpty(datas.getList())) {
            return resps;
        }

        //获取组织用户信息
        GetMemberSimpleInfoReq getMemberSimpleInfoReq = new GetMemberSimpleInfoReq();
        getMemberSimpleInfoReq.setOrgId(orgId);
        getMemberSimpleInfoReq.setType(1);
        List<MemberSimpleInfo> userInfos = userCenterApi.getMemberSimpleInfo(getMemberSimpleInfoReq).getData().getData();

        //获取所有权限组
        List<AppPerGroupListItem> appPerGroupListItems= appPermissionApi.getAppPermissionGroupList(orgId, appId).getData();
        Map<Long, AppPerGroupListItem> roleMap = com.polaris.lesscode.util.MapUtils.toMap(AppPerGroupListItem::getId, appPerGroupListItems);
        Long editRole = 0l;
        for (AppPerGroupListItem appPerGroupListItem : appPerGroupListItems) {
            if (appPerGroupListItem.getLangCode().equals(AppPerDefaultGroupLangCode.PROJECT_MEMBER.getCode())) {
                //-1是默认的负责人，关注人，确认人等 目前阶段默认为编辑者
                editRole = appPerGroupListItem.getId();
//                roleMap.put(-1l, appPerGroupListItem);
            }
        }
        //把-1替换成编辑者
        if (!Objects.equals(editRole, 0l)) {
            Long finalEditRole = editRole;
            allFieldMap.forEach((key, val)->{
                if (val.contains(-1l)) {
                    val.remove(-1l);
                    val.add(finalEditRole);
                }
            });
        }

        Map<String, Member> memberMap = new LinkedHashMap<>();

        Map<String, List<Long>> memberRoleMap = new LinkedHashMap<>();
        for (Map<String, Object> stringObjectMap : datas.getList()) {
            stringObjectMap.forEach((k,v)->{
                if (allFieldMap.containsKey(k)) {
                    if (v instanceof Collection) {
                        List<MemberResp> memberResps = JSON.parseArray(JSON.toJSONString(v), MemberResp.class);
                        for (MemberResp memberResp : memberResps) {
                            if (Objects.equals(memberResp.getType(), AppConsts.MEMBER_USER_TYPE) && Objects.equals(memberResp.getId(), 0l)) {
                                continue;
                            }
                            String key = memberResp.getType() + memberResp.getId();
                            List<Long> roles = new ArrayList<>(allFieldMap.get(k));
                            if (memberRoleMap.containsKey(key)) {
                                memberRoleMap.get(key).addAll(roles);
                            } else {
                                memberRoleMap.put(key, roles);
                            }
                            if (!memberMap.containsKey(key)) {
                                memberMap.put(key, new Member(memberResp.getId(), memberResp.getName(), memberResp.getAvatar(), memberResp.getType(), memberResp.getStatus(), memberResp.getIsDelete(), false, false, false));
                            }
                        }
                    }
                }
            });
        }
        if (allUser) {
            unfoldMembers(orgId, memberRoleMap, memberMap, userInfos);
        }

        List<Member> members = new ArrayList<>();
        memberMap.forEach((k,v)->{
            if (memberRoleMap.containsKey(k)) {
                List<AppPerGroupListItem> roleGroups= new ArrayList<>();
                List<Long> roles = memberRoleMap.get(k).stream().distinct().collect(Collectors.toList());
                for (Long aLong : roles) {
                    if (roleMap.containsKey(aLong)) {
                        roleGroups.add(roleMap.get(aLong));
                    }
                }
                Member member = v;
                v.setPerGroups(roleGroups);
                members.add(v);
            }
        });
        assemblyMembers(members, orgId);

        List<AppMemberResp> results = new ArrayList<>();
        for (Member member: members){
            results.add(new AppMemberResp(member));
        }
        results.sort(AppMemberResp::compareTo);
//        results = distinctMembers(results);
        return results;
    }

    private void assemblyMembers(List<Member> members, Long orgId){
        List<Long> userIds = new ArrayList<>();
        Map<Long, UserInfoResp> userMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(members)){
            for (Member member: members){
                if (Objects.equals(member.getType(), AppConsts.MEMBER_USER_TYPE)){
                    userIds.add(member.getId());
                }
            }
        }
        UserListByIdsReq userListByIdsReq = new UserListByIdsReq();
        userListByIdsReq.setOrgId(orgId);
        userListByIdsReq.setIds(userIds);
        List<UserInfoResp> userInfoRespList = userCenterApi.getAllUserListByIds(userListByIdsReq).getData();
        if (CollectionUtils.isNotEmpty(userInfoRespList)){
            userMap = com.polaris.lesscode.util.MapUtils.toMap(UserInfoResp::getId, userInfoRespList);
        }

        if (CollectionUtils.isNotEmpty(members)){
            for (Member member: members){
                if (Objects.equals(member.getType(), AppConsts.MEMBER_USER_TYPE)){
                    UserInfoResp userInfoResp = userMap.get(member.getId());
                    if (userInfoResp != null){
                        member.setName(userInfoResp.getName());
                        member.setStatus(userInfoResp.getStatus());
                        member.setIsDelete(userInfoResp.getIsDelete());
                        member.setAvatar(userInfoResp.getAvatar());
                    }
                }
            }
        }
    }

    private void unfoldMembers(Long orgId, Map<String, List<Long>> memberRoleMap, Map<String, Member> memberMap, List<MemberSimpleInfo> userInfos) {
        Map<Long, MemberSimpleInfo> users = com.polaris.lesscode.util.MapUtils.toMap(MemberSimpleInfo::getId, userInfos);

        GetDeptUserIdsReq getDeptUserIdsReq = new GetDeptUserIdsReq();
        getDeptUserIdsReq.setOrgId(orgId);
        Map<Long, List<Long>> deptUserIds = userCenterApi.getDeptUserIds(getDeptUserIdsReq).getData().getData();
        if (!com.polaris.lesscode.util.MapUtils.isEmpty(deptUserIds)) {
            memberMap.forEach((k,v)->{
                if (Objects.equals(v.getType(), AppConsts.MEMBER_DEPT_TYPE)){
                    if (deptUserIds.containsKey(v.getId())) {
                        String key = memberMap.get(k).getType() + memberMap.get(k).getId();
                        for (Long aLong : deptUserIds.get(v.getId())) {
                            String userKey = AppConsts.MEMBER_USER_TYPE + aLong;
                            if (memberRoleMap.containsKey(key)) {
                                if (memberRoleMap.containsKey(userKey)) {
                                    memberRoleMap.get(userKey).addAll(memberRoleMap.get(key));
                                } else {
                                    memberRoleMap.put(userKey, memberRoleMap.get(key));
                                }
                            }
                            if (!memberMap.containsKey(aLong)) {
                                MemberSimpleInfo userInfo = users.get(aLong);
                                if (userInfo != null) {
                                    memberMap.put(userKey, new Member(userInfo.getId(), userInfo.getName(), null, AppConsts.MEMBER_USER_TYPE, 1, 2, false, false, false));
                                }
                            }
                        }
                    }
                    memberMap.remove(k);
                }
            });
        }
    }


}
