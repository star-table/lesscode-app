package com.polaris.lesscode.app.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.polaris.lesscode.app.entity.AppPackageRelation;
import com.polaris.lesscode.consts.CommonConsts;

import java.util.List;

/**
 * @Author: Liu.B.J
 * @Data: 2020/8/31 11:23
 * @Modified:
 */
public interface AppPackageRelationMapper extends BaseMapper<AppPackageRelation> {

    default List<AppPackageRelation> getList(Long orgId, Long userId){
        return selectList(new LambdaQueryWrapper<AppPackageRelation>()
                .eq(AppPackageRelation :: getOrgId, orgId)
                .eq(AppPackageRelation :: getRelationId, userId)
                .eq(AppPackageRelation :: getDelFlag, CommonConsts.NO_DELETE));
    }

    default Integer del(Long id){
        LambdaUpdateWrapper<AppPackageRelation> updateWrapper = new LambdaUpdateWrapper<AppPackageRelation>()
                .set(AppPackageRelation :: getDelFlag, CommonConsts.DELETED)
                .eq(AppPackageRelation :: getId, id);
        return update(null, updateWrapper);
    }

}
