package com.polaris.lesscode.app.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.polaris.lesscode.app.enums.AppTemplateType;
import com.polaris.lesscode.app.enums.TemplateResourceType;
import com.polaris.lesscode.app.req.ApplyTemplateReq;
import com.polaris.lesscode.app.req.CreateTemplateReq;
import com.polaris.lesscode.app.resp.AppTemplateResourceResp;
import com.polaris.lesscode.app.resp.AppTemplateResp;
import com.polaris.lesscode.app.service.AppTemplateService;
import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Api(tags="应用模板")
@RestController
@RequestMapping("/app/api/v1/templates")
public class AppTemplateController {

	@Autowired
	private AppTemplateService appTemplateService;

	@ApiOperation(value="创建模板", notes="创建模板")
	@PostMapping
	public Result<AppTemplateResp> create(@Validated @RequestBody CreateTemplateReq req){
		return Result.ok(appTemplateService.create(RequestContext.currentOrgId(), RequestContext.currentUserId(), req));
//		return Result.ok(appTemplateService.create(1750l, 1152l, req));
	}

	@ApiOperation("删除模板")
	@DeleteMapping("/{id}")
	public Result<?> delete(@PathVariable("id") Long id){
		return Result.ok(appTemplateService.delete(RequestContext.currentOrgId(), RequestContext.currentUserId(), id));
	}

	@ApiOperation(value="查询模板列表", notes="查询模板列表")
	@GetMapping
	public Result<List<AppTemplateResp>> list(
			@ApiParam("模板id") @RequestParam(required = false) Long categoryId,
			@ApiParam("标签id") @RequestParam(required = false) Long tagId,
			@ApiParam("模板名称") @RequestParam(required = false) String name,
			@ApiParam("使用范围, 1，公共，2 团队") @RequestParam(required = false) Integer usageRange
	){
		return Result.ok(appTemplateService.list(RequestContext.currentOrgId(), categoryId, tagId, name, usageRange));
	}

	@ApiOperation("获取最近使用的模板列表")
	@GetMapping("recents")
	public Result<List<AppTemplateResp>> recents(
			@ApiParam("最近使用的条目数") @RequestParam(required = false) Integer size
	){
		return Result.ok(appTemplateService.recents(RequestContext.currentOrgId(), RequestContext.currentUserId(), size));
	}

	@ApiOperation(value="应用模板", notes="应用模板")
	@PostMapping("/apply")
	public Result<?> apply(@Validated @RequestBody ApplyTemplateReq req){
		return Result.ok(appTemplateService.apply(RequestContext.currentOrgId(), RequestContext.currentUserId(), req.getTemplateId(), req.getParentId(), false, req.getSaveSampleFlag(), false));
	}

	@ApiOperation("获取模板可选资源列表")
	@GetMapping("/resources")
	public Result<List<AppTemplateResourceResp>> resources(){
		List<AppTemplateResourceResp> results = new ArrayList<>();
		for (TemplateResourceType type: TemplateResourceType.values()){
			results.add(new AppTemplateResourceResp(type.getCode(), type.getDesc()));
		}
		return Result.ok(results);
	}

}
