package com.polaris.lesscode.app.internal.controller;

import com.polaris.lesscode.app.internal.api.AppVersionApi;
import com.polaris.lesscode.app.internal.req.AppVersionAddReq;
import com.polaris.lesscode.app.internal.req.AppVersionUpdateReq;
import com.polaris.lesscode.app.internal.resp.AppVersionResp;
import com.polaris.lesscode.app.internal.service.AppVersionInternalService;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AppVersionInternalController implements AppVersionApi{

	@Autowired
	private AppVersionInternalService appVersionService;
	
	@ApiOperation(value="获取应用版本列表", notes="获取应用版本列表")
	@GetMapping("/getAppVersionList")
	public Result<List<AppVersionResp>> getAppVersionList(
			@RequestParam("appId") Long appId, 
			@RequestParam(required = false) Integer type, 
			@RequestParam(required = false) Integer status) {
		return Result.ok(appVersionService.list(appId, type, status));
	}
	
	@ApiOperation(value="添加应用版本", notes="添加应用版本")
	@PostMapping("/addAppVersion")
	public Result<AppVersionResp> addAppVersion(
			@RequestParam("appId") Long appId, 
			@RequestBody AppVersionAddReq req){
		return Result.ok(appVersionService.add(appId, req));
	}
	
	@ApiOperation(value="更新应用版本", notes="更新应用版本")
	@PostMapping("/updateAppVersion")
	public Result<?> updateAppVersion(
			@RequestParam("appId") Long appId, 
			@RequestParam("appVersionId") Long appVersionId, 
			@RequestBody AppVersionUpdateReq req){
		appVersionService.update(appId, appVersionId, req);
		return Result.ok();
	}

}
