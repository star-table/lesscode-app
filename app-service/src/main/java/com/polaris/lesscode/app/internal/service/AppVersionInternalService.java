package com.polaris.lesscode.app.internal.service;

import java.util.List;
import java.util.Properties;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.polaris.lesscode.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.polaris.lesscode.app.config.RedisConfig;
import com.polaris.lesscode.app.consts.CacheKeyConsts;
import com.polaris.lesscode.app.entity.AppVersion;
import com.polaris.lesscode.app.internal.enums.AppVersionStatus;
import com.polaris.lesscode.app.internal.req.AppVersionAddReq;
import com.polaris.lesscode.app.internal.req.AppVersionUpdateReq;
import com.polaris.lesscode.app.internal.resp.AppVersionResp;
import com.polaris.lesscode.app.mapper.AppVersionMapper;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.util.ConvertUtil;
import com.polaris.lesscode.app.vo.ResultCode;

@Service
public class AppVersionInternalService extends ServiceImpl<AppVersionMapper, AppVersion> {

	@Autowired
	private AppVersionMapper appVersionMapper;
	
	@Autowired
	private RedisConfig redisConfig;
	
	public List<AppVersionResp> list(Long appId, Integer type, Integer status){
		LambdaQueryWrapper<AppVersion> query = new LambdaQueryWrapper<>();
		query.eq(AppVersion :: getAppId, appId);
		query.eq(AppVersion :: getDelFlag, CommonConsts.NO_DELETE);
		if(type != null) {
			query.eq(AppVersion :: getType, type);
		}
		if(status != null) {
			query.eq(AppVersion :: getStatus, status);
		}
		query.orderByDesc(AppVersion :: getVersion);
		
		List<AppVersion> list = appVersionMapper.selectList(query);
		List<AppVersionResp> resps = ConvertUtil.convertList(list, AppVersionResp.class);
		return resps;
	}
	
	public AppVersionResp add(Long appId, AppVersionAddReq addReq) {
		if(StringUtils.isBlank(addReq.getConfig())) {
			throw new BusinessException(ResultCode.APP_FORM_CONFIG_IS_EMPTY_ERROR.getCode(), ResultCode.APP_FORM_CONFIG_IS_EMPTY_ERROR.getMessage());
		}
		
		Properties properties = new Properties();
		properties.setProperty("appId", String.valueOf(appId));
		properties.setProperty("type", String.valueOf(addReq.getType()));
		
		AppVersion appVersion = redisConfig.synchronizedx(CacheKeyConsts.APP_VERSION_ADD_LOCK, properties, () -> {
			if(addReq.getStatus().equals(AppVersionStatus.DRAFT.getStatus()) || addReq.getStatus().equals(AppVersionStatus.USING.getStatus())) {
				AppVersion version = appVersionMapper.get(appId, addReq.getType(), addReq.getStatus());
				if(version != null) {
					return version;
				}
			}
			AppVersion version = new AppVersion();
			version.setAppId(appId);
			version.setConfig(addReq.getConfig());
			version.setStatus(addReq.getStatus());
			version.setType(addReq.getType());
			version.setVersion(System.currentTimeMillis());
			appVersionMapper.insert(version);
			return version;
		});
		return ConvertUtil.convert(appVersion, AppVersionResp.class);
	}
	
	public void update(Long appId, Long appVersionId, AppVersionUpdateReq updateReq) {
		if(StringUtils.isBlank(updateReq.getConfig())) {
			throw new BusinessException(ResultCode.APP_FORM_CONFIG_IS_EMPTY_ERROR.getCode(), ResultCode.APP_FORM_CONFIG_IS_EMPTY_ERROR.getMessage());
		}
		
		AppVersion updateValue = new AppVersion();
		updateValue.setConfig(updateReq.getConfig());
		appVersionMapper.update(updateValue, new LambdaUpdateWrapper<AppVersion>().eq(AppVersion :: getAppId, appId).eq(AppVersion :: getId, appVersionId));
	}
}
