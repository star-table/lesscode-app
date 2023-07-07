package com.polaris.lesscode.app.internal.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.polaris.lesscode.app.entity.AppPackage;
import com.polaris.lesscode.app.internal.resp.AppPackageResp;
import com.polaris.lesscode.app.mapper.AppPackageMapper;
import com.polaris.lesscode.app.service.AppPackageRelationService;
import com.polaris.lesscode.app.vo.ResultCode;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppPkgInternalService extends ServiceImpl<AppPackageMapper, AppPackage> {

	@Autowired
	private AppPackageRelationService appPackageRelService;

	public List<AppPackageResp> packageRespList(Long orgId) {
		List<AppPackageResp> responseList = null;
		List<AppPackage> list = this.baseMapper.appPkgList(orgId);
		responseList = ConvertUtil.convertList(list, AppPackageResp.class);
		return responseList;
	}

	public AppPackageResp get(Long pkgId) {
		AppPackage appPackage = this.baseMapper.get(pkgId);
		if(appPackage == null) {
			throw new BusinessException(ResultCode.APP_PACKAGE_NOT_EXIST.getCode(), ResultCode.APP_PACKAGE_NOT_EXIST.getMessage());
		}
		return ConvertUtil.convert(appPackage, AppPackageResp.class);
	}

}
