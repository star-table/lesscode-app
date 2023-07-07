package com.polaris.lesscode.app.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.polaris.lesscode.app.entity.AppWorkFlow;
import com.polaris.lesscode.consts.CommonConsts;

public interface AppWorkFlowMapper extends BaseMapper<AppWorkFlow>{

	default AppWorkFlow getByProcessKey(String processKey) {
		return selectOne(new LambdaQueryWrapper<AppWorkFlow>()
				.eq(AppWorkFlow :: getProcessKey, processKey)
				.eq(AppWorkFlow :: getDelFlag, CommonConsts.NO_DELETE)
				.last(" limit 1"));
	}

	default AppWorkFlow getByAppId(Long appId) {
		return selectOne(new LambdaQueryWrapper<AppWorkFlow>()
				.eq(AppWorkFlow :: getAppId, appId)
				.eq(AppWorkFlow :: getDelFlag, CommonConsts.NO_DELETE)
				.last(" limit 1"));
	}

}
