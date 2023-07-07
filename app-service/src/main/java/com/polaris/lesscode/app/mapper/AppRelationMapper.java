package com.polaris.lesscode.app.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.polaris.lesscode.app.entity.AppPackageRelation;
import com.polaris.lesscode.app.entity.AppRelation;
import com.polaris.lesscode.consts.CommonConsts;

import java.util.List;

public interface AppRelationMapper extends BaseMapper<AppRelation>{

    default List<AppRelation> getList(Long orgId, Long userId){
        return selectList(new LambdaQueryWrapper<AppRelation>()
                .eq(AppRelation :: getOrgId, orgId)
                .eq(AppRelation :: getRelationId, userId)
                .eq(AppRelation :: getDelFlag, CommonConsts.NO_DELETE));
    }

}
