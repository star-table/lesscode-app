package com.polaris.lesscode.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.polaris.lesscode.agg.internal.feign.AggProvider;
import com.polaris.lesscode.app.bo.AfterMoveBo;
import com.polaris.lesscode.app.entity.App;
import com.polaris.lesscode.app.entity.AppPackage;
import com.polaris.lesscode.app.enums.AppStatus;
import com.polaris.lesscode.app.enums.NearMoveType;
import com.polaris.lesscode.app.enums.ReferenceType;
import com.polaris.lesscode.app.enums.YesOrNo;
import com.polaris.lesscode.app.mapper.AppMapper;
import com.polaris.lesscode.app.mapper.AppPackageMapper;
import com.polaris.lesscode.app.req.AppPackageAddReq;
import com.polaris.lesscode.app.req.AppPackageUpdateReq;
import com.polaris.lesscode.app.req.MoveAppPackageReq;
import com.polaris.lesscode.app.resp.*;
import com.polaris.lesscode.app.utils.PermissionUtil;
import com.polaris.lesscode.app.vo.ResultCode;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.permission.internal.enums.AppVisibilityScope;
import com.polaris.lesscode.permission.internal.model.OrgUserPermissionContext;
import com.polaris.lesscode.permission.internal.model.resp.AppPerItem;
import com.polaris.lesscode.permission.internal.model.resp.AppPkgPerItem;
import com.polaris.lesscode.permission.internal.model.resp.SimpleAppPermissionResp;
import com.polaris.lesscode.permission.internal.model.resp.UserAppPermissionListResp;
import com.polaris.lesscode.util.ConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-08-03 11:35 上午
 */
@Slf4j
@Service
public class AppPackageService extends ServiceImpl<AppPackageMapper, AppPackage> {

    @Autowired
    private GroupService groupService;

    @Autowired
    private AppPackageRelationService appPackageRelService;

    @Autowired
    private AppMemberService appRelService;

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private AppService appService;

//    @Autowired
//    private AggProvider aggProvider;

    @Autowired
    private PermissionUtil permissionUtil;

    public AppPackageListResp packageRespList(Long orgId, Long userId) {
        // mock数据
        AppPackageListResp resp = new AppPackageListResp();
        /*List<AppResp> appList = ConvertUtil.convertList(
                appMapper.selectList(new LambdaQueryWrapper<App>()
                        .eq(App :: getOrgId, orgId)
                        .eq(App :: getDelFlag, CommonConsts.NO_DELETE)), AppResp.class);
        List<AppPackageResp> appPkgList = ConvertUtil.convertList(this.baseMapper.appPkgList(orgId), AppPackageResp.class);
        resp.setAppList(appList);
        resp.setAppPkgList(appPkgList);
        return resp;*/
        UserAppPermissionListResp userAppPerListResp = permissionUtil.getUserOfPackagePerList(orgId, userId).getData();
        resp.setCreatable(userAppPerListResp.getCreatable());
        Map<Long, AppPkgPerItem> perPkgMap = new HashMap<>();
        List<Long> pkgIds = new ArrayList<>();
        Map<Long, AppPerItem> perAppMap = new HashMap<>();
        List<Long> appIds = new ArrayList<>();
        if(! CollectionUtils.isEmpty(userAppPerListResp.getAppPkgList())){
            for (AppPkgPerItem appPkgPerItem : userAppPerListResp.getAppPkgList()) {
                perPkgMap.put(appPkgPerItem.getAppPackageId(), appPkgPerItem);
                pkgIds.add(appPkgPerItem.getAppPackageId());
            }
        }
        if(! CollectionUtils.isEmpty(userAppPerListResp.getAppList())){
            for (AppPerItem appPerItem : userAppPerListResp.getAppList()) {
                perAppMap.put(appPerItem.getAppId(), appPerItem);
                appIds.add(appPerItem.getAppId());
            }
        }


        // 所有父任务id
        List<App> apps = appMapper.selectList(new LambdaQueryWrapper<App>().eq(App::getOrgId, orgId).eq(App::getDelFlag, CommonConsts.FALSE));
        Map<Long, Long> childIdMap = new HashMap<>();	// child -> parent
        for (App app: apps){
            childIdMap.put(app.getId(), app.getParentId());
        }
        Queue<Long> queue = new LinkedBlockingQueue<>(appIds);
        Long childAppId = null;
        while((childAppId = queue.poll()) != null){
            Long parentId = childIdMap.get(childAppId);
            if (Objects.nonNull(parentId) && parentId > 0 && ! appIds.contains(parentId)){
                queue.add(parentId);
                appIds.add(parentId);
            }
        }

        List<AppPackageResp> appPackageResps = ConvertUtil.convertList(this.baseMapper.appPkgListByIds(pkgIds), AppPackageResp.class);
        List<AppPackageRelationResp> appPkgRelationResps = appPackageRelService.getList(pkgIds, orgId, userId);
        List<AppResp> appResps = ConvertUtil.convertList(appMapper.getListByIdsWithoutProjects(orgId, appIds), AppResp.class);
        if(! CollectionUtils.isEmpty(appPackageResps)){
            for (AppPackageResp appPackageResp : appPackageResps) {
                List<Map> listPkgRelMap = new ArrayList<>();
                appPkgRelationResps.forEach(p -> {
                    if(p.getPkgId().equals(appPackageResp.getId())){
                        Map<String, Object> map = new HashMap<>();
                        map.put("type", p.getType());
                        listPkgRelMap.add(map);
                    }
                });
                appPackageResp.setPkgTypes(listPkgRelMap);
                AppPkgPerItem pkgPerItem = perPkgMap.get(appPackageResp.getId());
                if(pkgPerItem != null){
                    appPackageResp.setManagePkg(pkgPerItem.getManagePkg());
                    appPackageResp.setEditable(pkgPerItem.getEditable());
                    appPackageResp.setDeletable(pkgPerItem.getDeletable());
                }
            }
        }
        if(! CollectionUtils.isEmpty(appResps)){
            for (AppResp appResp : appResps) {
                List<Map> listAppRelMap = new ArrayList<>();
                appResp.setAppTypes(listAppRelMap);
                AppPerItem appPerItem = perAppMap.get(appResp.getId());
                if(appPerItem != null){
                    // 此处针对crm
                    if(! Objects.equals(appPerItem.getShow(), Boolean.TRUE)){
                        appResp.setHidden(YesOrNo.YES.getCode());
                    }
                    appResp.setEditable(appPerItem.getEditable());
                    appResp.setDeletable(appPerItem.getDeletable());
                }
                if(AppStatus.DISABLE.getCode().equals(appResp.getStatus())){// 禁用
                    appResp.setHidden(YesOrNo.YES.getCode());
                }
            }
        }
        resp.setAppPkgList(appPackageResps);
        resp.setAppList(appResps);

        //add aggregation table
        List<Long> finalPkgIds = new ArrayList<>();
        if (appPackageResps != null) {
            for (AppPackageResp appPackageResp : appPackageResps) {
                Long pkgId = appPackageResp.getId();
                finalPkgIds.add(pkgId);
            }
        }
//        Map<String, List<AggResp>> finalMap = aggProvider.getPackageAggregation(finalPkgIds).getData();
//        for(Map.Entry<String, List<AggResp>> entry: finalMap.entrySet()) {
//            List<AggResp> aggResps = entry.getValue();
//            if (aggResps != null) {
//                for (AggResp aggResp : aggResps) {
//                    AppResp appResp = new AppResp();
//                    appResp.setId(aggResp.getId());
//                    appResp.setName(aggResp.getName());
//                    appResp.setOrgId(aggResp.getOrgId());
//                    appResp.setPkgId(aggResp.getPkgId());
//                    appResp.setType(AppType.AGG.getCode());
//                    appResps.add(appResp);
//                }
//            }
//
//        }
        return resp;
    }

    public AppPackageResp get(Long pkgId, Long orgId, Long userId) {
        AppPackage appPackage = this.baseMapper.get(pkgId);
        if(appPackage == null) {
            throw new BusinessException(ResultCode.APP_PACKAGE_NOT_EXIST.getCode(), ResultCode.APP_PACKAGE_NOT_EXIST.getMessage());
        }
        OrgUserPermissionContext orgUserPermissionContext = new OrgUserPermissionContext(permissionUtil.getUserPermission(orgId, userId).getData());
        if(! orgUserPermissionContext.hasQueryAppPackagePermission(pkgId)){
            throw new BusinessException(ResultCode.NO_GET_APPPKG_PERMISSION.getCode(), ResultCode.NO_GET_APPPKG_PERMISSION.getMessage());
        }
        AppPackageResp resp = ConvertUtil.convert(appPackage, AppPackageResp.class);
        SimpleAppPermissionResp simpleAppPermissionResp = permissionUtil.getSimpleAppPackagePermission(orgId, pkgId).getData();
        if(simpleAppPermissionResp != null){
            resp.setScope(simpleAppPermissionResp.getScope());
            resp.setMembers(simpleAppPermissionResp.getMembers());
        }
        return resp;
    }

    public AppPackageResp get(Long pkgId) {
        AppPackage appPackage = this.baseMapper.get(pkgId);
        if(appPackage == null) {
            throw new BusinessException(ResultCode.APP_PACKAGE_NOT_EXIST.getCode(), ResultCode.APP_PACKAGE_NOT_EXIST.getMessage());
        }
        return ConvertUtil.convert(appPackage, AppPackageResp.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public AppPackageResp addAppPackage(Long orgId, Long userId, AppPackageAddReq addReq){
        OrgUserPermissionContext orgUserPermissionContext = new OrgUserPermissionContext(permissionUtil.getUserPermission(orgId, userId).getData());
        if(! orgUserPermissionContext.hasCreateAppPackagePermission()){
            throw new BusinessException(ResultCode.NO_ADD_APPPKG_PERMISSION.getCode(), ResultCode.NO_ADD_APPPKG_PERMISSION.getMessage());
        }
        AppPackage appPackage = new AppPackage();
        appPackage.setCreator(userId);
        appPackage.setUpdator(userId);
        appPackage.setOrgId(orgId);
        appPackage.setParentId(addReq.getParentId());
        appPackage.setDelFlag(CommonConsts.NO_DELETE);
        appPackage.setName(addReq.getName());
        boolean suc = this.baseMapper.insert(appPackage) > 0;
        if (suc){
            if(! permissionUtil.saveOrUpdateAppPkgPer(addReq.getParentId(), appPackage.getId(), orgId, userId, addReq.getScope() == null ? AppVisibilityScope.ALL.getCode() : addReq.getScope(), addReq.getMembers()).getData()){
                throw new BusinessException(ResultCode.SAVE_OR_UPDATEAPPPKG_PERMISSION_FAIL.getCode(), ResultCode.SAVE_OR_UPDATEAPPPKG_PERMISSION_FAIL.getMessage());
            }
            return ConvertUtil.convert(appPackage, AppPackageResp.class);
        }
        throw new BusinessException(ResultCode.APP_PACKAGE_ADD_FAIL.getCode(), ResultCode.APP_PACKAGE_ADD_FAIL.getMessage());
    }

    @Transactional(rollbackFor = Exception.class)
    public AppPackageResp modifyAppPackage(Long pkgId, Long orgId, Long userId, AppPackageUpdateReq updateReq){
        AppPackage appPkg = this.baseMapper.get(pkgId);
        if(appPkg == null){
            throw new BusinessException(ResultCode.APP_PACKAGE_NOT_EXIST.getCode(), ResultCode.APP_PACKAGE_NOT_EXIST.getMessage());
        }

        OrgUserPermissionContext orgUserPermissionContext = new OrgUserPermissionContext(permissionUtil.getUserPermission(orgId, userId).getData());
        if(! orgUserPermissionContext.hasManageAppPackagePermission(pkgId)){
            throw new BusinessException(ResultCode.NO_MODIFY_APPPKG_PERMISSION.getCode(), ResultCode.NO_MODIFY_APPPKG_PERMISSION.getMessage());
        }
        if(! StringUtils.isBlank(updateReq.getName())){
            appPkg.setName(updateReq.getName());
        }
        if(! StringUtils.isBlank(updateReq.getRemark())){
            appPkg.setRemark(updateReq.getRemark());
        }
        appPkg.setUpdator(userId);
        this.baseMapper.updateById(appPkg);
//        if(! permissionUtil.saveOrUpdateAppPkgPer(, pkgId, orgId, userId, updateReq.getScope() == null ? AppVisibilityScope.ALL.getCode() : updateReq.getScope(), updateReq.getMembers()).getData()){
//            throw new BusinessException(ResultCode.SAVE_OR_UPDATEAPPPKG_PERMISSION_FAIL.getCode(), ResultCode.SAVE_OR_UPDATEAPPPKG_PERMISSION_FAIL.getMessage());
//        }
        return ConvertUtil.convert(appPkg, AppPackageResp.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer deleteAppPackage(Long pkgId, Long orgId, Long userId){
        AppPackage appPkg = this.baseMapper.get(pkgId);
        if(appPkg == null){
            throw new BusinessException(ResultCode.APP_PACKAGE_NOT_EXIST.getCode(), ResultCode.APP_PACKAGE_NOT_EXIST.getMessage());
        }

        OrgUserPermissionContext orgUserPermissionContext = new OrgUserPermissionContext(permissionUtil.getUserPermission(orgId, userId).getData());
        if (!orgUserPermissionContext.hasDeleteAppPackagePermission(pkgId)) {
            throw new BusinessException(ResultCode.NO_DELETE_APPPKG_PERMISSION.getCode(), ResultCode.NO_DELETE_APPPKG_PERMISSION.getMessage());
        }
        // 获取当前包下所有子包
        List<Long> pkgIds = new ArrayList<>();
        pkgIds.add(pkgId);
        pkgIds = getPkgIds(pkgIds, pkgIds);
        List<App> listApps = appMapper.getByPkgIds(pkgIds);
        List<Long> appIds = listApps.stream().map(App::getId).collect(Collectors.toList());
        if (!permissionUtil.deleteAppPkgPer(pkgIds, appIds, orgId, userId)) {
            throw new BusinessException(ResultCode.NO_DEL_APPPKG_PERMISSION.getCode(), ResultCode.NO_DEL_APPPKG_PERMISSION.getMessage());
        }

        // 删除级联的应用包
        List<Long> associationPkgIds = new ArrayList<>();
        associationPkgIds.add(pkgId);
        List<AppPackage> appPackages = this.baseMapper.appPkgList(orgId);
        Map<Long, Long> getIdNameMap = appPackages.stream().collect(Collectors.toMap(AppPackage::getId, AppPackage::getParentId));
        Stack<Long> stack = new Stack<>();
        stack.push(pkgId);
        while (! stack.empty()){
            Long parentId = stack.pop();
            getIdNameMap.forEach((k,v)->{
                if(parentId.equals(v)){
                    associationPkgIds.add(k);
                    stack.push(k);
                }
            });
        }
        appService.batchDelete(orgId, associationPkgIds, userId);
        return batchDel(associationPkgIds, userId);
    }

    private Integer batchDel(List<Long> pkgIds, Long userId){
        LambdaUpdateWrapper<AppPackage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AppPackage :: getDelFlag, CommonConsts.DELETED)
                .set(AppPackage :: getUpdator, userId)
                .in(AppPackage :: getId, pkgIds);
        return this.baseMapper.update(null, updateWrapper);
    }

    public List<AppPackageResp> getPkgList(Long parentId){
        List<AppPackageResp> responseList = new ArrayList<>();
        List<AppPackage> listPkg = this.baseMapper.selectList(new LambdaQueryWrapper<AppPackage>()
                .eq(AppPackage :: getParentId, parentId)
                .eq(AppPackage :: getDelFlag, CommonConsts.NO_DELETE));
        responseList = ConvertUtil.convertList(listPkg, AppPackageResp.class);
        return responseList;
    }

    public void moveAppPkg(Long orgId, Long userId, MoveAppPackageReq req){
        OrgUserPermissionContext orgUserPermissionContext = new OrgUserPermissionContext(permissionUtil.getUserPermission(orgId, userId).getData());
        if(! orgUserPermissionContext.hasAllPermission()){
            throw new BusinessException(ResultCode.APP_PACKAGE_NO_MOVE_PERMISSION.getCode(), ResultCode.APP_PACKAGE_NO_MOVE_PERMISSION.getMessage());
        }
        if(NearMoveType.formatOrNull(req.getType()) == null){
            throw new BusinessException(ResultCode.MOVE_REQ_ERROR.getCode(), ResultCode.MOVE_REQ_ERROR.getMessage());
        }
    	AppPackage updateEntity = new AppPackage();
        if(req.getBeforeId() != null || req.getAfterId() != null) {
        	boolean isBeforeReference = req.getBeforeId() != null;
            AfterMoveBo afterMove = groupService.updateReferenceSort(
                    orgId,
        			isBeforeReference ? req.getBeforeId() : req.getAfterId(),
                    req.getType(),
        			isBeforeReference ? ReferenceType.BEFORE.getType() : ReferenceType.AFTER.getType());
        	if(afterMove != null) {
        		updateEntity.setParentId(afterMove.getParentId());
//        		updateEntity.setSort(afterMove.getSort());
        	}
        }else{// 无参照物
            AfterMoveBo afterMove = groupService.updateNoReferenceSort(req.getParentId());
            if(afterMove != null) {
                updateEntity.setParentId(afterMove.getParentId());
//                updateEntity.setSort(afterMove.getSort());
            }
        }
        
    	if(StringUtils.isNotBlank(req.getName())) {
        	updateEntity.setName(req.getName());
        }
        
    	updateEntity.setId(req.getId());
       this.baseMapper.updateById(updateEntity);
    }

    public List<Long> getPkgIds(List<Long> pkgIds, List<Long> parentIds){
        List<Long> ids = this.baseMapper.getPkgIdsByParentIds(parentIds, CommonConsts.NO_DELETE);
        if(! CollectionUtils.isEmpty(ids)){
            pkgIds.addAll(ids);
            getPkgIds(pkgIds, ids);
        }else{
            return pkgIds;
        }
        return pkgIds;
    }

}
