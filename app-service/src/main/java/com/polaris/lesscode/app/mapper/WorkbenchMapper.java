package com.polaris.lesscode.app.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.polaris.lesscode.app.entity.Workbench;
import com.polaris.lesscode.app.enums.YesOrNo;
import com.polaris.lesscode.consts.CommonConsts;

import java.util.List;

/**
 * @author: Liu.B.J
 * @date: 2020/12/15 13:59
 * @description:
 */
public interface WorkbenchMapper extends BaseMapper<Workbench> {

    default Workbench get(Long id) {
        return selectOne(new LambdaQueryWrapper<Workbench>()
                .eq(Workbench :: getId, id)
                .eq(Workbench :: getDelFlag, CommonConsts.NO_DELETE)
                .last(" limit 1"));
    }

    default List<Workbench> getList(Long userId, Long orgId){
        return selectList(new LambdaQueryWrapper<Workbench>()
                .eq(Workbench :: getUserId, userId)
                .eq(Workbench :: getOrgId, orgId)
                .eq(Workbench :: getDelFlag, YesOrNo.NO.getCode()));
    }

}
