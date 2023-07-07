package com.polaris.lesscode.app.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.polaris.lesscode.app.entity.AppPackage;
import com.polaris.lesscode.consts.CommonConsts;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-08-03 11:24 上午
 */
public interface AppPackageMapper extends BaseMapper<AppPackage> {

    default AppPackage get(Long pkgId){
        return selectOne(new LambdaQueryWrapper<AppPackage>()
                .eq(AppPackage :: getId, pkgId)
                .eq(AppPackage :: getDelFlag, CommonConsts.NO_DELETE)
                .last(" limit 1"));
    }

    default List<AppPackage> getByParentId(Long id){
        if(id == null || id == 0){
            return null;
        }
        return selectList(new LambdaQueryWrapper<AppPackage>()
                .eq(AppPackage :: getParentId, id)
                .eq(AppPackage :: getDelFlag, CommonConsts.NO_DELETE));
    }

    @Select("<script>" +
            "select id from lc_app_pkg where parent_id in (" +
            "<foreach collection='parentIds' separator=',' item='id'>" +
            "#{id} " +
            "</foreach> " +
            ") and del_flag = #{delFlag}" +
            "</script>")
    public List<Long> getPkgIdsByParentIds(@Param("parentIds") List<Long> parentIds, @Param("delFlag") Integer delFlag);

    default List<AppPackage> getListById(Long id){
        if(id == null || id == 0){
            return null;
        }
        return selectList(new LambdaQueryWrapper<AppPackage>()
                .eq(AppPackage :: getParentId, id)
                .eq(AppPackage :: getDelFlag, CommonConsts.NO_DELETE));
    }

    default Integer del(Long pkgId){
        LambdaUpdateWrapper<AppPackage> updateWrapper = new LambdaUpdateWrapper<AppPackage>()
                .set(AppPackage :: getDelFlag, CommonConsts.DELETED)
                .eq(AppPackage :: getId, pkgId);
        return update(null, updateWrapper);
    }

    default List<AppPackage> appPkgList(Long orgId){
        return selectList(new LambdaQueryWrapper<AppPackage>()
                .eq(AppPackage :: getOrgId, orgId)
                .eq(AppPackage :: getDelFlag, CommonConsts.NO_DELETE));
    }

    default List<AppPackage> appPkgListByIds(List<Long> pkgIds){
        if(CollectionUtils.isEmpty(pkgIds)){
            return new ArrayList<>();
        }
        return selectList(new LambdaQueryWrapper<AppPackage>()
                .in(AppPackage::getId, pkgIds)
                .eq(AppPackage::getDelFlag, CommonConsts.NO_DELETE));
    }

    default AppPackage getMaxSort(Long orgId){
        return selectOne(new LambdaQueryWrapper<AppPackage>()
                .eq(AppPackage :: getDelFlag, CommonConsts.NO_DELETE)
                .eq(AppPackage :: getOrgId, orgId)
                .orderByDesc(AppPackage :: getSort)
                .last(" limit 1"));
    }

    default AppPackage getMinSort(Long orgId){
        return selectOne(new LambdaQueryWrapper<AppPackage>()
                .eq(AppPackage :: getDelFlag, CommonConsts.NO_DELETE)
                .eq(AppPackage :: getOrgId, orgId)
                .orderByAsc(AppPackage :: getSort)
                .last(" limit 1"));
    }

}
