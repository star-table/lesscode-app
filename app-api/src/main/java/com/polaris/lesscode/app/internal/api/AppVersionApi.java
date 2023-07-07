package com.polaris.lesscode.app.internal.api;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.polaris.lesscode.app.internal.req.AppVersionAddReq;
import com.polaris.lesscode.app.internal.req.AppVersionUpdateReq;
import com.polaris.lesscode.app.internal.resp.AppVersionResp;
import com.polaris.lesscode.vo.Result;

/**
 * 应用版本服务
 *
 * @date 2020年9月10日
 */
@Api(value="应用版本管理（内部调用）", tags={"应用版本管理（内部调用）"})
@RequestMapping("/app/inner/api/v1/app/versions")
public interface AppVersionApi {

	/**
	 * 获取应用版本列表
	 * 
	 * @param appId	应用id
	 * @param type	类型
	 * @param status	状态
	 * @return
	 */
	@ApiOperation(value="获取应用版本列表", notes="获取应用版本列表")
	@GetMapping("/get-app-versionList")
	public Result<List<AppVersionResp>> getAppVersionList(
			@RequestParam(value = "appId") Long appId, 
			@RequestParam(value = "type", required = false) Integer type, 
			@RequestParam(value = "status", required = false) Integer status);
	
	/**
	 * 添加应用版本
	 * 
	 * @param appId	应用id
	 * @param req	请求结构体
	 * @return
	 */
	@ApiOperation(value="添加应用版本", notes="添加应用版本")
	@PostMapping("/add-app-version")
	public Result<AppVersionResp> addAppVersion(
			@RequestParam(value = "appId") Long appId, 
			@RequestBody AppVersionAddReq req);
	
	/**
	 * 更新应用版本
	 * 
	 * @param appId 应用gid
	 * @param appVersionId	应用版本id
	 * @param req 请求结构体
	 * @return
	 */
	@ApiOperation(value="更新应用版本", notes="更新应用版本")
	@PostMapping("/update-app-version")
	public Result<?> updateAppVersion(
			@RequestParam(value = "appId") Long appId, 
			@RequestParam(value = "appVersionId") Long appVersionId, 
			@RequestBody AppVersionUpdateReq req);
}
