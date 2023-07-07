package com.polaris.lesscode.app.controller;

import com.polaris.lesscode.app.entity.AppAction;
import com.polaris.lesscode.app.req.CreateCommentReq;
import com.polaris.lesscode.app.resp.AppActionResp;
import com.polaris.lesscode.app.service.AppActionService;
import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.vo.Page;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags="应用动作")
@RestController
@RequestMapping("/app/api/v1/actions")
public class AppActionController {

	@Autowired
	private AppActionService appActionService;

	@ApiOperation(value="获取应用动作列表", notes="获取应用动作列表（各枚举具体值待定，前端可先自定义mock")
	@GetMapping
	public Result<Page<AppActionResp>> list(
			@ApiParam("操作对象ID") @RequestParam(required = false) Long objId,
			@ApiParam("对象类型，1：表单，2：仪表盘，3：文件夹，") @RequestParam(required = false) Integer objType,
			@ApiParam("动作类型, 1: 创建，2: 修改，3：删除，4：评论") @RequestParam(required = false) Integer action,
			@ApiParam("操作人") @RequestParam(required = false) Long operator,
			@ApiParam("数据id") @RequestParam(required = false) Long dataId,
			@ApiParam("开始id，会查询大于此id的数据") @RequestParam(required = false) Long startId,
			@ApiParam("结束id，会查询小于此id的数据") @RequestParam(required = false) Long endId,
			@ApiParam("开始时间, 格式2021-01-25 16:37:54") @RequestParam(required = false) String startTime,
			@ApiParam("结束时间，格式2021-01-25 16:37:54") @RequestParam(required = false) String endTime,
			@ApiParam("查询顺序，默认降序") @RequestParam(required = false, defaultValue = "false") boolean isAsc,
			@ApiParam("单次查询数量") @RequestParam(required = false) Integer size,
			@ApiParam("变更的字段") @RequestParam(required = false) String changedKey
	) {
		return Result.ok(appActionService.getActions(RequestContext.currentOrgId(), objId, objType, action, operator, dataId, startId, endId, startTime, endTime, isAsc, size, changedKey));
	}

	@ApiOperation(value="创建评论", notes="创建评论")
	@PostMapping("/comments")
	public Result<AppAction> createComments(@Validated @RequestBody CreateCommentReq req){
		return Result.ok(appActionService.createComments(RequestContext.currentOrgId(), RequestContext.currentUserId(), req));
	}

}
