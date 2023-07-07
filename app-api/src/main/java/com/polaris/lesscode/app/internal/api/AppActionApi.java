package com.polaris.lesscode.app.internal.api;

import com.polaris.lesscode.app.internal.req.CreateAppActionReq;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 应用动作内部接口
 *
 * @Author Nico
 * @Date 2021/1/26 20:31
 **/
@Api(tags="应用动作内部接口定义")
@RequestMapping("/app/inner/api/v1/actions")
public interface AppActionApi {


	/**
	 * 创建动作api
	 *
	 * @param orgId 组织id
	 * @param operatorId 操作人id
	 * @param req 创建动作请求结构体
	 **/
	@ApiOperation(value="创建动作", notes="创建动作")
	@PostMapping("")
	Result<Boolean> createAction(
			@RequestParam(value = "orgId") Long orgId,
			@RequestParam(value = "operatorId", required = false) Long operatorId,
			@RequestBody CreateAppActionReq req);

}
