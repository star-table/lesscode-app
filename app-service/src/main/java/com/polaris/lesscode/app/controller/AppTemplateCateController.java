package com.polaris.lesscode.app.controller;

import com.polaris.lesscode.app.enums.AppTemplateType;
import com.polaris.lesscode.app.mapper.AppTemplateCateMapper;
import com.polaris.lesscode.app.req.ApplyTemplateReq;
import com.polaris.lesscode.app.req.CreateTemplateReq;
import com.polaris.lesscode.app.resp.AppTemplateCateResp;
import com.polaris.lesscode.app.resp.AppTemplateResp;
import com.polaris.lesscode.app.service.AppTemplateCateService;
import com.polaris.lesscode.app.service.AppTemplateService;
import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Api(tags="应用模板分类")
@RestController
@RequestMapping("/app/api/v1/template-categories")
public class AppTemplateCateController {

	@Autowired
	private AppTemplateCateService appTemplateCateService;


	@ApiOperation(value="查询模板分类列表", notes="查询模板分类列表")
	@GetMapping
	public Result<List<AppTemplateCateResp>> list(
			@ApiParam("可使用范围,1: 模板市场，2：本组织") @RequestParam(required = false) Integer usableRange,
			@ApiParam("模板类型") @RequestParam(required = false) String type
	){
		return Result.ok(appTemplateCateService.getCateList());
	}


}
