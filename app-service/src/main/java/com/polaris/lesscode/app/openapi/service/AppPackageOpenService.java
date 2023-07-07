package com.polaris.lesscode.app.openapi.service;

//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.polaris.lesscode.app.entity.App;
//import com.polaris.lesscode.app.entity.AppPackage;
//import com.polaris.lesscode.app.enums.YesOrNo;
//import com.polaris.lesscode.app.mapper.AppMapper;
//import com.polaris.lesscode.app.mapper.AppPackageMapper;
//import com.polaris.lesscode.app.openapi.req.AppPackageOpenAddReq;
//import com.polaris.lesscode.app.openapi.req.AppPackageOpenUpdateReq;
//import com.polaris.lesscode.app.resp.AppPackageResp;
//import com.polaris.lesscode.app.service.AppPackageService;
//import com.polaris.lesscode.app.service.GroupService;
//import com.polaris.lesscode.app.utils.PermissionUtil;
//import com.polaris.lesscode.app.vo.ResultCode;
//import com.polaris.lesscode.consts.CommonConsts;
//import com.polaris.lesscode.exception.BusinessException;
//import com.polaris.lesscode.permission.internal.enums.AppVisibilityScope;
//import com.polaris.lesscode.permission.internal.model.OrgUserPermissionContext;
//import com.polaris.lesscode.util.ConvertUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * @author: Liu.B.J
// * @data: 2020/10/20 20:28
// * @modified:
// */
//
//@Slf4j
//@Service
//public class AppPackageOpenService extends ServiceImpl<AppPackageMapper, AppPackage> {
//
//    @Autowired
//    private GroupService groupService;
//
//    @Autowired
//    private AppMapper appMapper;
//
//
//    @Autowired
//    private PermissionUtil permissionUtil;
//
//    @Autowired
//    private AppPackageService appPackageService;
//
//    @Transactional(rollbackFor = Exception.class)
//    public AppPackageResp addAppPackage(Long orgId, Long userId, AppPackageOpenAddReq addReq){
//        OrgUserPermissionContext orgUserPermissionContext = new OrgUserPermissionContext(permissionUtil.getUserPermission(orgId, userId).getData());
//        if(! orgUserPermissionContext.hasCreateAppPackagePermission()){
//            throw new BusinessException(ResultCode.NO_ADD_APPPKG_PERMISSION.getCode(), ResultCode.NO_ADD_APPPKG_PERMISSION.getMessage());
//        }
//        AppPackage appPackage = new AppPackage();
//        appPackage.setCreator(userId);
//        appPackage.setUpdator(userId);
//        appPackage.setOrgId(orgId);
//        appPackage.setParentId(addReq.getParentId() == null ? 0L : addReq.getParentId());
//        appPackage.setDelFlag(CommonConsts.NO_DELETE);
//        appPackage.setName(addReq.getName());
//        appPackage.setExternalPkg(YesOrNo.YES.getCode());
//        appPackage.setLinkUrl(addReq.getLinkUrl());
//        boolean suc = this.baseMapper.insert(appPackage) > 0;
//        if (suc){
//            if(! permissionUtil.saveOrUpdateAppPkgPer(addReq.getParentId(), appPackage.getId(), orgId, userId, addReq.getScope() == null ? AppVisibilityScope.ALL.getCode() : addReq.getScope(), addReq.getMembers()).getData()){
//                throw new BusinessException(ResultCode.SAVE_OR_UPDATEAPPPKG_PERMISSION_FAIL.getCode(), ResultCode.SAVE_OR_UPDATEAPPPKG_PERMISSION_FAIL.getMessage());
//            }
//            return ConvertUtil.convert(appPackage, AppPackageResp.class);
//        }
//        throw new BusinessException(ResultCode.APP_PACKAGE_ADD_FAIL.getCode(), ResultCode.APP_PACKAGE_ADD_FAIL.getMessage());
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    public AppPackageResp modifyAppPackage(Long pkgId, Long orgId, Long userId, AppPackageOpenUpdateReq updateReq){
//        AppPackage appPkg = this.baseMapper.get(pkgId);
//        if(appPkg == null){
//            throw new BusinessException(ResultCode.APP_PACKAGE_NOT_EXIST.getCode(), ResultCode.APP_PACKAGE_NOT_EXIST.getMessage());
//        }
//        OrgUserPermissionContext orgUserPermissionContext = new OrgUserPermissionContext(permissionUtil.getUserPermission(orgId, userId).getData());
//        if(! orgUserPermissionContext.hasManageAppPackagePermission(pkgId)){
//            throw new BusinessException(ResultCode.NO_MODIFY_APPPKG_PERMISSION.getCode(), ResultCode.NO_MODIFY_APPPKG_PERMISSION.getMessage());
//        }
//        appPkg.setName(updateReq.getName());
//        if(! StringUtils.isBlank(updateReq.getLinkUrl())){
//            appPkg.setLinkUrl(updateReq.getLinkUrl());
//        }
//        appPkg.setUpdator(userId);
//        boolean suc = this.baseMapper.updateById(appPkg) > 0;
//        if(! suc){
//            throw new BusinessException(ResultCode.APP_PACKAGE_MODIFY_FAIL.getCode(), ResultCode.APP_PACKAGE_MODIFY_FAIL.getMessage());
//        }
////        if(! permissionUtil.saveOrUpdateAppPkgPer(pkgId, orgId, userId, updateReq.getScope() == null ? AppVisibilityScope.ALL.getCode() : updateReq.getScope(), updateReq.getMembers()).getData()){
////            throw new BusinessException(ResultCode.SAVE_OR_UPDATEAPPPKG_PERMISSION_FAIL.getCode(), ResultCode.SAVE_OR_UPDATEAPPPKG_PERMISSION_FAIL.getMessage());
////        }
//        return ConvertUtil.convert(appPkg, AppPackageResp.class);
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    public Integer deleteAppPackage(Long pkgId, Long orgId, Long userId){
//        AppPackage appPkg = this.baseMapper.get(pkgId);
//        if(appPkg == null){
//            throw new BusinessException(ResultCode.APP_PACKAGE_NOT_EXIST.getCode(), ResultCode.APP_PACKAGE_NOT_EXIST.getMessage());
//        }
//        // 判断该应用包下有没有嵌套应用包或应用
//        if (!CollectionUtils.isEmpty(appMapper.getByPkgId(pkgId)) || !CollectionUtils.isEmpty(this.baseMapper.getListById(appPkg.getParentId()))) {
//            throw new BusinessException(ResultCode.APP_PACKAGE_DEL_FAIL.getCode(), ResultCode.APP_PACKAGE_DEL_FAIL.getMessage());
//        }
//        OrgUserPermissionContext orgUserPermissionContext = new OrgUserPermissionContext(permissionUtil.getUserPermission(orgId, userId).getData());
//        if (!orgUserPermissionContext.hasDeleteAppPackagePermission(pkgId)) {
//            throw new BusinessException(ResultCode.NO_DELETE_APPPKG_PERMISSION.getCode(), ResultCode.NO_DELETE_APPPKG_PERMISSION.getMessage());
//        }
//        // 获取当前包下所有子包
//        List<Long> pkgIds = new ArrayList<>();
//        pkgIds.add(pkgId);
//        pkgIds = appPackageService.getPkgIds(pkgIds, pkgIds);
//        List<App> listApps = appMapper.getByPkgIds(pkgIds);
//        List<Long> appIds = listApps.stream().map(App::getId).collect(Collectors.toList());
//        if (!permissionUtil.deleteAppPkgPer(pkgIds, appIds, orgId, userId)) {
//            throw new BusinessException(ResultCode.NO_DEL_APPPKG_PERMISSION.getCode(), ResultCode.NO_DEL_APPPKG_PERMISSION.getMessage());
//        }
//        /*List<Long> appIds = appService.getAppIds(orgId, pkgId, null, null);
//        appService.delete(pkgId, userId);*/
//        appPkg.setDelFlag(CommonConsts.DELETED);
//        appPkg.setUpdator(userId);
//        int num = this.baseMapper.updateById(appPkg);
//       /* if(num > 0 && ! CollectionUtils.isEmpty(appIds)){
//            formProvider.deleteForm(appIds, userId);
//        }*/
//        return num;
//    }
//
//}
