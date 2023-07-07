package com.polaris.lesscode.app.utils;

import com.polaris.lesscode.permission.internal.api.AppPackagePermissionApi;
import com.polaris.lesscode.permission.internal.feign.AppPermissionProvider;
import com.polaris.lesscode.permission.internal.feign.PermissionProvider;
import com.polaris.lesscode.permission.internal.model.UserPermissionVO;
import com.polaris.lesscode.permission.internal.model.bo.ViewAuth;
import com.polaris.lesscode.permission.internal.model.req.*;
import com.polaris.lesscode.permission.internal.model.resp.*;
import com.polaris.lesscode.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: Liu.B.J
 * @data: 2020/9/24 13:06
 * @modified:
 */
@Component
public class PermissionUtil {

    @Autowired
    private AppPackagePermissionApi appPkgPermissionProvider;

    @Autowired
    private PermissionProvider permissionProvider;

    @Autowired
    private AppPermissionProvider appPermissionProvider;

    public List<AppPerGroupListItem> getAppPermissionGroupList(Long orgId, Long appId) {
        return appPermissionProvider.getAppPermissionGroupList(orgId, appId).getData();
    }

    public Boolean addAppMembers(AddAppMembersReq req) {
        return appPermissionProvider.addAppMembers(req).getData();
    }

    public AppAuthorityResp getAppAuth(long orgId, long appId, Long userId){
        return appPermissionProvider.getAppAuthority(orgId, appId, null, userId).getData();
    }

    /**
     * 执行创建/修改应用包权限
     *
     * @param appPkgId
     * @param orgId
     * @param userId
     * @param scope 权限范围
     * @param members 权限成员
     */
    public Result<Boolean> saveOrUpdateAppPkgPer(Long parentId, Long appPkgId, Long orgId, Long userId, Integer scope, List<PermissionMembersItemReq> members){
        ModifyAppPackagePermissionReq req = new ModifyAppPackagePermissionReq();
        req.setAppPackageId(appPkgId);
        req.setOrgId(orgId);
        req.setUserId(userId);
        req.setScope(scope);
        req.setParentPkgId(parentId);
        if(null == members){
            req.setMembers(new ArrayList<>());
        }else{
            req.setMembers(members);
        }
        return appPkgPermissionProvider.saveOrUpdateAppPackagePermission(req);
    }

    /**
     * 删除应用包权限
     *
     * @param appPkgIds
     * @param orgId
     * @param userId
     * @return
     */
    public boolean deleteAppPkgPer(List<Long> appPkgIds, List<Long> appIds, Long orgId, Long userId) {
        DeleteAppPackagePermissionReq req = new DeleteAppPackagePermissionReq();
        req.setOrgId(orgId);
        req.setAppPackageIds(appPkgIds);
        req.setAppIds(appIds);
        req.setUserId(userId);
        return appPkgPermissionProvider.deleteAppPackagePermission(req).getData();
    }

    /**
     * 获取应用包权限 简要信息
     *
     * @param orgId
     * @param appPkgId
     * @return
     */
    public Result<SimpleAppPermissionResp> getSimpleAppPackagePermission(Long orgId, Long appPkgId){
        return appPkgPermissionProvider.getSimpleAppPackagePermission(orgId, appPkgId);
    }

//    /**
//     * 获取应用权限组-操作权限
//     *
//     * @param orgId
//     * @param appId
//     * @param userId
//     * @return
//     */
//    public FromPerOptAuthVO getOptAuth(Long orgId, Long appId, Long userId) {
//        return appPermissionProvider.getOptAuth(orgId, appId, userId).getData();
//    }

    /**
     * 初始化应用权限组
     *
     * @return
     */
    public Result<Boolean> initDefaultPermissionGroup(InitAppPermissionReq req) {
        return appPermissionProvider.initAppPermission(req);
    }

    /**
     * 获取成员权限
     *
     * @param orgId
     * @param userId
     * @return
     */
    public Result<UserPermissionVO> getUserPermission(Long orgId, Long userId){
        return permissionProvider.getUserPermission(orgId, userId);
    }

    /**
     * 获取用户的应用包及应用权限
     *
     * @param orgId
     * @param userId
     * @return
     */
    public Result<UserAppPermissionListResp> getUserOfPackagePerList(Long orgId, Long userId){
        return permissionProvider.getUserHavePerList(orgId, userId);
    }

    /**
     * 删除应用时，同步的删除应用权限组
     *
     * @param appId
     * @param orgId
     * @param userId
     * @return
     */
    public boolean deleteAppPermission(Long appId, Long orgId, Long userId) {
        DeleteAppPermissionReq req = new DeleteAppPermissionReq();
        req.setAppId(appId);
        req.setOrgId(orgId);
        req.setUserId(userId);
        return appPermissionProvider.deleteAppPermission(req).getData();
    }

//    /**
//     * 修改表单字段配置
//     *
//     * @param orgId
//     * @param appId
//     * @param config
//     * @return
//     */
//    public boolean updateFormFieldConfig(Long orgId, Long appId, String config) {
//        return false;
////        UpdateFieldConfigReq updateFieldConfigReq = new UpdateFieldConfigReq();
////        updateFieldConfigReq.setAppId(appId);
////        updateFieldConfigReq.setOrgId(orgId);
////        updateFieldConfigReq.setConfig(config);
////        return appPermissionProvider.updateFormFieldConfig(updateFieldConfigReq).getData();
//    }

    /**
     * 获取可见的App视图列表ID
     *
     * @param orgId
     * @param appId
     * @param userId
     * @return
     */
    public Map<Long, ViewAuth> getUserAppViewList(Long orgId, Long appId, Long userId) {
        return appPermissionProvider.getViewAuth(orgId, appId, userId).getData().entrySet().stream().collect(Collectors.toMap(e -> Long.valueOf(e.getKey()), Map.Entry::getValue));
    }

}
