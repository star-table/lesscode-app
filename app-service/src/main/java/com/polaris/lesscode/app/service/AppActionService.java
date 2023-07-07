package com.polaris.lesscode.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.reflect.TypeToken;
import com.polaris.lesscode.app.bo.AppActionChanges;
import com.polaris.lesscode.app.entity.AppAction;
import com.polaris.lesscode.app.internal.enums.ActionObjType;
import com.polaris.lesscode.app.internal.enums.ActionType;
import com.polaris.lesscode.app.internal.req.CreateAppActionChanges;
import com.polaris.lesscode.app.internal.req.CreateAppActionReq;
import com.polaris.lesscode.app.mapper.AppActionMapper;
import com.polaris.lesscode.app.mapper.AppMapper;
import com.polaris.lesscode.app.req.CreateCommentReq;
import com.polaris.lesscode.app.resp.AppActionChangesResp;
import com.polaris.lesscode.app.resp.AppActionResp;
import com.polaris.lesscode.app.resp.UserResp;
import com.polaris.lesscode.app.utils.SecurityUtil;
import com.polaris.lesscode.app.vo.ResultCode;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.uc.internal.api.UserCenterApi;
import com.polaris.lesscode.uc.internal.req.DeptListByIdsReq;
import com.polaris.lesscode.uc.internal.req.RoleListByIdsReq;
import com.polaris.lesscode.uc.internal.req.UserListByIdsReq;
import com.polaris.lesscode.uc.internal.resp.DeptInfoResp;
import com.polaris.lesscode.uc.internal.resp.RoleInfoResp;
import com.polaris.lesscode.uc.internal.resp.UserInfoResp;
import com.polaris.lesscode.util.GsonUtils;
import com.polaris.lesscode.util.MapUtils;
import com.polaris.lesscode.vo.Page;
import com.polaris.lesscode.vo.Result;
import com.polaris.lesscode.workflow.internal.api.WorkflowApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * app action service
 *
 * @Author Nico
 * @Date 2021/1/25 10:55
 **/
@Slf4j
@Service
public class AppActionService extends ServiceImpl<AppActionMapper, AppAction> {

    @Autowired
    private AppActionMapper appActionMapper;

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private WorkflowApi workflowApi;

    @Autowired
    private UserCenterApi userCenterApi;

    /**
     * 获取应用Actions列表
     * TODO 权限控制
     *
     * @Author Nico
     * @Date 2021/1/25 11:42
     **/
    public Page<AppActionResp> getActions(
            Long orgId,
            Long objId,
            Integer objType,
            Integer action,
            Long operator,
            Long dataId,
            Long startId,
            Long endId,
            String startTime,
            String endTime,
            boolean isAsc,
            Integer size,
            String changedKey
    ) {
        LambdaQueryWrapper<AppAction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AppAction::getOrgId, orgId);
        queryWrapper.eq(AppAction::getDelFlag, CommonConsts.FALSE);
        if (objId != null){
            queryWrapper.eq(AppAction::getObjId, objId);
        }
        if (objType != null){
            queryWrapper.eq(AppAction::getObjType, objType);
        }
        if (action != null){
            queryWrapper.eq(AppAction::getAction, action);
        }
        if (operator != null){
            queryWrapper.eq(AppAction::getOperator, operator);
        }
        if (endId != null){
            queryWrapper.lt(AppAction::getId, endId);
        }
        if (startId != null){
            queryWrapper.gt(AppAction::getId, startId);
        }
        if (dataId != null){
            queryWrapper.eq(AppAction::getDataId, dataId);
        }
        if (StringUtils.isNotBlank(endTime)){
            queryWrapper.le(AppAction::getOperateTime, endTime);
        }
        if (StringUtils.isNotBlank(startTime)){
            queryWrapper.ge(AppAction::getOperateTime, startTime);
        }
        if (isAsc){
            queryWrapper.orderByAsc(AppAction::getId);
        }else{
            queryWrapper.orderByDesc(AppAction::getId);
        }
        if (StringUtils.isNotBlank(changedKey)){
            String[] keys = changedKey.split(",");
            queryWrapper.and(wrapper -> {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < keys.length; i ++){
                    String key = keys[i];
                    if (SecurityUtil.isSafelyColumn(key)){
                        builder.append("JSON_SEARCH(`changes`, 'one', '").append(key).append("', null, '$[*].key')");
                        if (i != keys.length - 1){
                            builder.append(" or ");
                        }
                    }
                }
                wrapper.last(builder.toString());
                return wrapper;
            });
        }
        if (size == null || size < 1 || size >= 3000){
            size = 3000;
        }
        IPage<AppAction> appActionPage = appActionMapper.selectPage(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, size), queryWrapper);
        Long total = appActionPage.getTotal();
        List<AppAction> appActions = appActionPage.getRecords();
        List<AppActionResp> appActionRespList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(appActions)){

            // 收集必要的信息，方便下边批处理
            Set<Long> userIds = new HashSet<>();
            Set<Long> roleIds = new HashSet<>();
            Set<Long> deptIds = new HashSet<>();
            Set<Long> nodeIds = new HashSet<>();
            Map<Long, List<AppActionChanges>> appActionChangesMap = new LinkedHashMap<>();
            for(AppAction appAction: appActions){
                userIds.add(appAction.getOperator());
                if(StringUtils.isNotBlank(appAction.getChanges())){
                    List<AppActionChanges> changes = GsonUtils.readValue(appAction.getChanges(), new TypeToken<List<AppActionChanges>>() {}.getType());
                    if (CollectionUtils.isNotEmpty(changes)){
                        for(AppActionChanges change: changes){
                            if ("user".equals(change.getType())){
                                collectId(userIds, change.getAfter());
                                collectId(userIds, change.getBefore());
                            }else if("dept".equals(change.getType())){
                                collectId(deptIds, change.getAfter());
                                collectId(deptIds, change.getBefore());
                            }else if(Objects.equals("member", change.getType())){
                                parseMemberIds(userIds, deptIds, roleIds, change.getAfter());
                                parseMemberIds(userIds, deptIds, roleIds, change.getBefore());
                            }else if (Objects.equals("node", change.getType())){
                                collectId(nodeIds, change.getBefore());
                                collectId(nodeIds, change.getAfter());
                            }
                        }
                        appActionChangesMap.put(appAction.getId(), changes);
                    }
                }
            }

            // 获取所有节点
            if (CollectionUtils.isNotEmpty(nodeIds)){

            }

            // 批处理用户信息
            Map<Long, UserInfoResp> userMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(userIds)){
                UserListByIdsReq userListByIdsReq = new UserListByIdsReq();
                userListByIdsReq.setIds(new ArrayList<>(userIds));
                userListByIdsReq.setOrgId(orgId);
                Result<List<UserInfoResp>> userListResp = userCenterApi.getAllUserListByIds(userListByIdsReq);
                userMap = MapUtils.toMap(UserInfoResp::getId, userListResp.getData());
            }

            // 批处理部门信息
            Map<Long, DeptInfoResp> deptMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(deptIds)){
                DeptListByIdsReq deptListByIdsReq = new DeptListByIdsReq();
                deptListByIdsReq.setIds(new ArrayList<>(deptIds));
                deptListByIdsReq.setOrgId(orgId);
                Result<List<DeptInfoResp>> deptListResp = userCenterApi.getAllDeptListByIds(deptListByIdsReq);
                deptMap = MapUtils.toMap(DeptInfoResp::getId, deptListResp.getData());
            }

            // 批处理角色
            Map<Long, RoleInfoResp> roleMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(roleIds)){
                RoleListByIdsReq roleListByIdsReq = new RoleListByIdsReq();
                roleListByIdsReq.setIds(new ArrayList<>(roleIds));
                roleListByIdsReq.setOrgId(orgId);
                Result<List<RoleInfoResp>> roleListResp = userCenterApi.getAllRoleListByIds(roleListByIdsReq);
                roleMap = MapUtils.toMap(RoleInfoResp::getId, roleListResp.getData());
            }

            // 批处理changes
            // TODO 临时策略，所有字段变动都会显示，用户和部门两块实时查寻，其余产品定义changes的内容(例如富文本不会被展示出来
            Map<Long, List<AppActionChangesResp>> appActionChangesRespMap = new LinkedHashMap<>();
            for(Map.Entry<Long, List<AppActionChanges>> entry: appActionChangesMap.entrySet()){
                List<AppActionChangesResp> respList = new ArrayList<>();
                for (AppActionChanges change: entry.getValue()){
                    AppActionChangesResp changesResp = new AppActionChangesResp();
                    changesResp.setKey(change.getKey());
                    changesResp.setName(change.getName());
                    if("user".equals(change.getType())){
                        changesResp.setAfter(userMap.get(parseId(change.getAfter())));
                        changesResp.setBefore(userMap.get(parseId(change.getBefore())));
                    }else if ("dept".equals(change.getType())){
                        changesResp.setAfter(deptMap.get(parseId(change.getAfter())));
                        changesResp.setBefore(deptMap.get(parseId(change.getBefore())));
                    }else if ("member".equals(change.getType())){
                        changesResp.setAfter(parseMember(userMap, deptMap, roleMap, change.getAfter()));
                        changesResp.setBefore(parseMember(userMap, deptMap, roleMap, change.getBefore()));
                    }else{
                        changesResp.setAfter(change.getAfter());
                        changesResp.setBefore(change.getBefore());
                    }
                    respList.add(changesResp);
                }
                appActionChangesRespMap.put(entry.getKey(), respList);
            }

            // 组装action响应数据
            for(AppAction appAction: appActions){
                AppActionResp appActionResp = new AppActionResp();
                appActionResp.setId(appAction.getId());
                appActionResp.setObjId(appAction.getObjId());
                appActionResp.setObjType(appAction.getObjType());
                appActionResp.setOrgId(orgId);
                appActionResp.setSubform(StringUtils.isNotBlank(appAction.getSubformKey()));
                appActionResp.setDataId(appAction.getDataId());
                appActionResp.setSubformDataId(appAction.getSubformDataId());
                appActionResp.setSubformKey(appAction.getSubformKey());
                appActionResp.setSubformName(appAction.getSubformName());

                ActionObjType actionType = ActionObjType.formatOrNull(appAction.getObjType());
                if (actionType != null){
                    appActionResp.setObjName(actionType.getDesc());
                }
                appActionResp.setChanges(appActionChangesRespMap.get(appAction.getId()));

                ActionType actionEnum = ActionType.formatOrNull(appAction.getAction());
                if (actionEnum != null){
                    appActionResp.setAction(actionEnum.getCode());
                    appActionResp.setActionName(actionEnum.getDesc());
                    appActionResp.setIsComment(ActionType.COMMENT.equals(actionEnum));
                }
                UserInfoResp userInfoResp = userMap.get(appAction.getOperator());
                if (userInfoResp != null){
                    UserResp userResp = new UserResp();
                    userResp.setId(userInfoResp.getId());
                    userResp.setName(userInfoResp.getName());
                    userResp.setAvatar(userInfoResp.getAvatar());
                    appActionResp.setOperator(userResp);
                }
                appActionResp.setOperateTime(appAction.getOperateTime());
                appActionRespList.add(appActionResp);
            }
        }
        return new Page<>(total, appActionRespList);
    }

    private long parseId(Object o){
        if(o instanceof Number){
            return ((Number) o).longValue();
        }else if(o instanceof String && StringUtils.isNumeric(o.toString())){
            return Long.parseLong(o.toString());
        }
        return 0;
    }

    private void parseMemberIds(Set<Long> userIds, Set<Long> deptIds, Set<Long> roleIds, Object list){
        if(list instanceof Collection){
            for(Object o: (Collection<?>) list){
                if(o instanceof String){
                    String v = o.toString();
                    if(v.startsWith("U_")){
                        userIds.add(Long.valueOf(v.substring(2)));
                    }else if(v.startsWith("D_")){
                        deptIds.add(Long.valueOf(v.substring(2)));
                    }else if(v.startsWith("R_")){
                        roleIds.add(Long.valueOf(v.substring(2)));
                    }
                }
            }
        }
    }

    private List<Object> parseMember(Map<Long, UserInfoResp> userMap, Map<Long, DeptInfoResp> deptMap, Map<Long, RoleInfoResp> roleMap, Object list){
        List<Object> result = new ArrayList<>();
        if(list instanceof Collection){
            for(Object o: (Collection<?>) list){
                if(o instanceof String){
                    String v = o.toString();
                    if(v.startsWith("U_")){
                        UserInfoResp userInfoResp = userMap.get(Long.valueOf(v.substring(2)));
                        if(userInfoResp != null){
                            result.add(userInfoResp);
                        }
                    }else if(v.startsWith("D_")){
                        DeptInfoResp deptInfoResp = deptMap.get(Long.valueOf(v.substring(2)));
                        if(deptInfoResp != null){
                            result.add(deptInfoResp);
                        }
                    }else if(v.startsWith("R_")){
                        RoleInfoResp roleInfoResp = roleMap.get(Long.valueOf(v.substring(2)));
                        if(roleInfoResp != null){
                            result.add(roleInfoResp);
                        }
                    }
                }
            }
        }
        return result;
    }

    private void collectId(Set<Long> ids, Object o){
        ids.add(parseId(o));
    }

    private void collectIds(Set<Long> ids, Object o){
        if(o instanceof Collection){
            for(Object subO: (Collection<?>) o){
                collectId(ids, subO);
            }
        }
    }

    public AppAction createComments(Long orgId, Long operatorId, CreateCommentReq req){
        String content = req.getContent();
        if(StringUtils.isBlank(content)){
            throw new BusinessException(ResultCode.APP_ACTION_COMMENT_CONTENT_IS_BLANK);
        }
        content = content.trim();
        if(content.length() > 5000){
            throw new BusinessException(ResultCode.APP_ACTION_COMMENT_CONTENT_IS_TOO_LONG);
        }

        // todo 抽到静态？
        Map<String, Object> before = new LinkedHashMap<>();
        Map<String, Object> after = new LinkedHashMap<>();
        after.put("comment", content);

        Map<String, String> names = new LinkedHashMap<>();
        names.put("comment", "评论");

        Map<String, String> types = new LinkedHashMap<>();
        types.put("comment", "text");

        CreateAppActionReq createAppActionReq = new CreateAppActionReq();
        createAppActionReq.setAction(ActionType.COMMENT.getCode());
        createAppActionReq.setObjId(req.getObjId());
        createAppActionReq.setObjType(req.getObjType());
        createAppActionReq.setNames(names);
        createAppActionReq.setTypes(types);

        CreateAppActionChanges changes = new CreateAppActionChanges();
        changes.setDataId(req.getDataId());
        changes.setBefore(before);
        changes.setAfter(after);
        createAppActionReq.setChanges(Collections.singletonList(changes));
        List<AppAction> actions = createAction(orgId, operatorId, createAppActionReq);
        if(CollectionUtils.isNotEmpty(actions)){
            return actions.get(0);
        }
        return null;
    }

    public List<AppAction> createAction(Long orgId, Long operatorId, CreateAppActionReq req){
        if(CollectionUtils.isEmpty(req.getChanges())){
            throw new BusinessException(ResultCode.APP_ACTION_CREATE_FAIL);
        }

        ActionObjType objType = ActionObjType.formatOrNull(req.getObjType());
        if (objType == null){
            throw new BusinessException(ResultCode.APP_ACTION_OBJ_INVALID);
        }

        ActionType appActionEnum = ActionType.formatOrNull(req.getAction());
        if(appActionEnum == null){
            throw new BusinessException(ResultCode.APP_ACTION_INVALID);
        }

        List<AppAction> actions = new ArrayList<>();
        for(CreateAppActionChanges changes: req.getChanges()){
            AppAction appAction = new AppAction();
            appAction.setOrgId(orgId);
            appAction.setAction(req.getAction());
            appAction.setObjId(req.getObjId());
            appAction.setObjType(req.getObjType());
            appAction.setDataId(changes.getDataId());
            appAction.setSubformDataId(changes.getSubformDataId());
            appAction.setSubformKey(changes.getSubformKey());
            appAction.setSubformName(changes.getSubformName());
            appAction.setOperator(operatorId);
            appAction.setCreator(operatorId);
            appAction.setUpdator(operatorId);
            appAction.setOperateTime(new Date());
            appAction.setBefore(GsonUtils.toJson(changes.getBefore()));
            appAction.setAfter(GsonUtils.toJson(changes.getAfter()));

            List<AppActionChanges> changesList = parseChanges(changes.getBefore(), changes.getAfter(), req.getNames(), req.getTypes());
            appAction.setChanges(CollectionUtils.isEmpty(changesList) ? "[]" : GsonUtils.toJson(changesList));

            actions.add(appAction);
        }

        if(! saveBatch(actions)){
            throw new BusinessException(ResultCode.APP_ACTION_CREATE_FAIL);
        }
        return actions;
    }

    private List<AppActionChanges> parseChanges(Map<String, Object> before, Map<String, Object> after, Map<String, String> names, Map<String, String> types){
        List<AppActionChanges> changesList = new ArrayList<>();
        if(before == null){
            before = new HashMap<>();
        }
        if(after == null){
            after = new HashMap<>();
        }
        for(Map.Entry<String, Object> beforeEntry: before.entrySet()){
            after.putIfAbsent(beforeEntry.getKey(), null);
        }
        for(Map.Entry<String, Object> afterEntry: after.entrySet()){
            Object beforeObj = before.get(afterEntry.getKey());
            Object afterObj = afterEntry.getValue();
            // 重复值也覆盖进去
//            if(beforeObj == null && afterObj == null){
//               continue;
//            }
//            if(beforeObj != null && beforeObj.equals(afterObj)){
//                continue;
//            }
            AppActionChanges changes = new AppActionChanges();
            changes.setKey(afterEntry.getKey());
            changes.setName(names.get(afterEntry.getKey()));
            changes.setBefore(beforeObj);
            changes.setAfter(afterObj);
            changes.setType(types.get(afterEntry.getKey()));
            changesList.add(changes);
        }
        return changesList;
    }
}



