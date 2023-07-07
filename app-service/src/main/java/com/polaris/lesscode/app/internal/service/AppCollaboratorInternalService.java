package com.polaris.lesscode.app.internal.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.polaris.lesscode.app.bo.Member;
import com.polaris.lesscode.app.consts.AppConsts;
import com.polaris.lesscode.app.entity.App;
import com.polaris.lesscode.app.mapper.AppMapper;
import com.polaris.lesscode.app.resp.AppMemberResp;
import com.polaris.lesscode.app.resp.MemberResp;
import com.polaris.lesscode.app.service.GoTableService;
import com.polaris.lesscode.app.vo.ResultCode;
import com.polaris.lesscode.dc.internal.dsl.Condition;
import com.polaris.lesscode.dc.internal.dsl.Conditions;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.internal.api.AppFormApi;
import com.polaris.lesscode.form.internal.api.AppValueApi;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.req.AppValueListReq;
import com.polaris.lesscode.form.internal.sula.FieldParam;
import com.polaris.lesscode.gotable.internal.feign.GoTableProvider;
import com.polaris.lesscode.gotable.internal.resp.TableSchemas;
import com.polaris.lesscode.permission.internal.api.AppPermissionApi;
import com.polaris.lesscode.permission.internal.enums.AppPerDefaultGroupLangCode;
import com.polaris.lesscode.permission.internal.model.resp.AppPerGroupListItem;
import com.polaris.lesscode.uc.internal.api.UserCenterApi;
import com.polaris.lesscode.uc.internal.req.GetMemberSimpleInfoReq;
import com.polaris.lesscode.uc.internal.req.GetUserDeptIdsReq;
import com.polaris.lesscode.uc.internal.resp.GetUserDeptIdsResp;
import com.polaris.lesscode.uc.internal.resp.MemberSimpleInfo;
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
public class AppCollaboratorInternalService {

    @Autowired
    private GoTableService goTableService;

    @Autowired
    private AppValueApi appValueApi;

    @Autowired
    private AppPermissionApi appPermissionApi;

    @Autowired
    private UserCenterApi userCenterApi;

    @Autowired
    private AppMapper appMapper;

    public List<Long> getUserCollaboratorRoleIds(Long orgId, Long appId, Long tableId, Long userId){
        App app = appMapper.get(appId);
        if (Objects.isNull(app)){
            throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " app:" + appId);
        }

        List<Long> resps = new ArrayList<>();
        //查看表头配置中作为协作人的字段
        TableSchemas tableSchemas = goTableService.readSchemaByAppId(appId, orgId, tableId, userId);
        Map<String, List<Long>> allFieldMap = new LinkedHashMap<>();
        for (JSONObject obj : tableSchemas.getColumns()) {
            FieldParam field = JSON.toJavaObject(obj,FieldParam.class);
            if (judgeHasCollaboratorRoles(field)) {
                Object collaboratorRolesObj = field.getField().getProps().get("collaboratorRoles");
                allFieldMap.put(field.getName(), objToList(collaboratorRolesObj));
            }
        }
        if (MapUtils.isEmpty(allFieldMap)) {
            return resps;
        }

        //获取人员部门
        GetUserDeptIdsReq getUserDeptIdsReq = new GetUserDeptIdsReq();
        getUserDeptIdsReq.setOrgId(orgId);
        getUserDeptIdsReq.setUserId(userId);
        GetUserDeptIdsResp getUserDeptIdsResp = userCenterApi.getUserDeptIds(getUserDeptIdsReq).getData();
        List<Long> userAllDeptIds = getUserDeptIdsResp.getDeptIds();
        userAllDeptIds.add(0l);

        //查询数据并组装
        AppValueListReq appValueListReq = new AppValueListReq();
        appValueListReq.setPage(0);
        appValueListReq.setSize(0);
        appValueListReq.setOrgId(orgId);
        List<Long> appIds = new ArrayList<>();
        appIds.add(appId);
        appValueListReq.setRedirectIds(appIds);
        List<String> allRelateMember = new ArrayList<>();
        allRelateMember.add(AppConsts.MEMBER_USER_TYPE + userId);
        for (Long userAllDeptId : userAllDeptIds) {
            allRelateMember.add(AppConsts.MEMBER_DEPT_TYPE + userAllDeptId);
        }
        //协作人字段中包含自己的
        List<Condition> conditions = new ArrayList<>();
        allFieldMap.forEach((key, val)->{
            Condition c = new Condition();
            c.setType(Conditions.VALUES_IN);
            c.setValues(allRelateMember.toArray());
            c.setColumn(key);
            conditions.add(c);
        });
        appValueListReq.setCondition(Conditions.or(conditions));
        Page<Map<String, Object>> datas =  appValueApi.filter(appId, appValueListReq).getData();
        if (CollectionUtils.isEmpty(datas.getList())) {
            return resps;
        }

        //获取所有权限组
        List<AppPerGroupListItem> appPerGroupListItems= appPermissionApi.getAppPermissionGroupList(orgId, appId).getData();
        Long editRole = 0l;
        for (AppPerGroupListItem appPerGroupListItem : appPerGroupListItems) {
            if (appPerGroupListItem.getLangCode().equals(AppPerDefaultGroupLangCode.PROJECT_MEMBER.getCode()) || appPerGroupListItem.getLangCode().equals(AppPerDefaultGroupLangCode.EDIT.getCode())) {
                //-1是默认的负责人，关注人，确认人等 目前阶段默认为编辑者
                editRole = appPerGroupListItem.getId();
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

        List<Long> allRoles = new ArrayList<>();
        for (Map<String, Object> stringObjectMap : datas.getList()) {
            stringObjectMap.forEach((k,v)->{
                if (allFieldMap.containsKey(k)) {
                    if (v instanceof Collection) {
                        List<Long> roles = new ArrayList<>(allFieldMap.get(k));

                        List<MemberResp> memberResps = JSON.parseArray(JSON.toJSONString(v), MemberResp.class);
                        for (MemberResp memberResp : memberResps) {
                            if ((Objects.equals(memberResp.getType(), AppConsts.MEMBER_USER_TYPE) && Objects.equals(memberResp.getId(), userId)) ||
                                    ((Objects.equals(memberResp.getType(), AppConsts.MEMBER_DEPT_TYPE) && userAllDeptIds.contains(memberResp.getId())))) {
                                allRoles.addAll(roles);
                                break;
                            }
                        }
                    }
                }
            });
        }

        return allRoles.stream().distinct().collect(Collectors.toList());
    }


    public static List<Long> objToList(Object obj) {
        List<Long> result = new ArrayList<>();
        if (obj instanceof ArrayList<?>) {
            for (Object o : (List<?>) obj) {
                if (Objects.isNull(o)) {
                    continue;
                }
                result.add(Long.valueOf(String.class.cast(o)));
            }
        }
        return result;
    }

    public boolean judgeHasCollaboratorRoles(FieldParam fieldParam) {
        Map<String, Object> props = fieldParam.getField().getProps();
        if (Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.DEPT.getFormFieldType()) || Objects.equals(fieldParam.getField().getType(), FieldTypeEnums.USER.getFormFieldType())){
            if (MapUtils.isNotEmpty(props) && props.containsKey("collaboratorRoles")){
                Object collaboratorRolesObj = props.get("collaboratorRoles");
                if (collaboratorRolesObj instanceof Collection && CollectionUtils.isNotEmpty((Collection<?>) collaboratorRolesObj)){
                    return true;
                }
            }
        }

        return false;
    }
}
