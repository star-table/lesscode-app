package com.polaris.lesscode.app.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.polaris.lesscode.app.entity.AppVersion;
import com.polaris.lesscode.consts.CommonConsts;

public interface AppVersionMapper extends BaseMapper<AppVersion>{

	default AppVersion get(Long appId, Integer type, Integer status) {
		return selectOne(new LambdaQueryWrapper<AppVersion>()
				.eq(AppVersion :: getAppId, appId)
				.eq(AppVersion :: getType, type)
				.eq(AppVersion :: getStatus, status)
				.eq(AppVersion :: getDelFlag, CommonConsts.NO_DELETE)
				.last(" limit 1"));
	}
}
