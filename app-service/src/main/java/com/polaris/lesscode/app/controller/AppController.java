package com.polaris.lesscode.app.controller;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.polaris.lesscode.app.req.*;
import com.polaris.lesscode.app.resp.AppListResp;
import com.polaris.lesscode.app.resp.AppPackageListResp;
import com.polaris.lesscode.app.resp.AppResp;
import com.polaris.lesscode.app.resp.MoveAppListResp;
import com.polaris.lesscode.app.resp.HiddenObjResp;
import com.polaris.lesscode.app.service.AppPackageService;
import com.polaris.lesscode.app.service.AppService;
import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.form.internal.req.AppFormExcelSaveReq;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

@Api(tags="应用管理")
@RestController
@RequestMapping("/app/api/v1/apps")
public class AppController {

	@Autowired
	private AppService appService;

	@Value("${hidden.config:default}")
	private String hiddenConfigFileName;

	@Value("${spring.profiles.active}")
	private String env;

	@Autowired
	private AppPackageService appPackageService;

	@ApiOperation(value="获取应用列表", notes="获取应用列表")
	@GetMapping
	public Result<AppListResp> list(
			@ApiParam("根据上级id获取应用列表") @RequestParam(required = false) Long parentId,
			@ApiParam("应用类型") @RequestParam(required = false) Integer type,
			@ApiParam("工作流应用标识，1代表获取工作流应用") @RequestParam(required = false) Integer workflowFlag,
			@ApiParam("收藏标识，1代表获取收藏的应用") @RequestParam(required = false) Integer starFlag,
			@ApiParam("是否需要查询所有app，不需要过滤条件") @RequestParam(required = false) Boolean isNeedAllApps,
			@ApiParam("模板id") @RequestParam(required = false) Long templateId) {
		if (Objects.equals(env, "stag")){
			AppPackageListResp resp = appPackageService.packageRespList(RequestContext.currentOrgId(), RequestContext.currentUserId());
			AppListResp appListResp = new AppListResp();
			appListResp.setAppListResp(resp.getAppList());
			return Result.ok(appListResp);
		}
		AppListResp resp = appService.list(RequestContext.currentOrgId(), RequestContext.currentUserId(), parentId, type, workflowFlag, starFlag, templateId, isNeedAllApps);
		return Result.ok(resp);
	}

	@ApiOperation(value="获取应用信息", notes="获取应用信息")
	@GetMapping("/{appId}")
	public Result<AppResp> getApp(@PathVariable("appId") Long appId) {
		AppResp resp = appService.get(appId, RequestContext.currentOrgId(), RequestContext.currentUserId());
		return Result.ok(resp);
	}

	@ApiOperation(value="添加应用", notes="添加应用")
	@PostMapping
	public Result<AppResp> add(@Validated @RequestBody AppAddReq req) {
		AppResp resp = appService.add(req, RequestContext.currentOrgId(), RequestContext.currentUserId());
		return Result.ok(resp);
	}

	@ApiOperation(value="通过excel导入保存应用表单", notes="通过excel导入保存应用表单")
	@PostMapping("/import")
	public Result<?> excelImportSave(@RequestBody AppFormExcelSaveReq req){
		return appService.excelSave(req, RequestContext.currentOrgId(), RequestContext.currentUserId());
	}

	@ApiOperation(value="通过表单设计保存应用表单", notes="通过表单设计保存应用表单")
	@PostMapping("/save")
	public Result<?> formDesignSave(@RequestBody AppFormSaveReq req){
		return appService.formSave(req, RequestContext.currentOrgId(), RequestContext.currentUserId());
	}

	@ApiOperation(value="编辑应用", notes="编辑应用")
	@PostMapping("/{appId}")
	public Result<AppResp> update(@PathVariable("appId") Long appId, @RequestBody AppUpdateReq req) {
		AppResp resp = appService.update(appId, req, RequestContext.currentOrgId(), RequestContext.currentUserId());
		return Result.ok(resp);
	}
	
	@ApiOperation(value="删除应用", notes="删除应用")
	@DeleteMapping("/{appId}")
	public Result<Boolean> delete(@PathVariable("appId") Long appId) {
		boolean suc = appService.delete(appId, RequestContext.currentOrgId(), RequestContext.currentUserId());
		return Result.ok(suc);
	}
	
	@ApiOperation(value="发布应用", notes="发布应用")
	@PutMapping("/{appId}")
	public Result<?> publish(@PathVariable("appId") Long appId) {
		appService.publish(appId, RequestContext.currentOrgId(), RequestContext.currentUserId());
		return Result.ok();
	}

	@ApiOperation(value="移动应用分组信息", notes="移动应用分组信息")
	@PatchMapping
	public Result<MoveAppListResp> move(@RequestBody MoveAppReq req){
		MoveAppListResp resp = appService.moveApp(RequestContext.currentOrgId(), RequestContext.currentUserId(), req);
		return Result.ok(resp);
	}

	@ApiOperation(value="移动应用分组信息", notes="移动应用分组信息")
	@PostMapping("/{appId}/switch-auth-type")
	public Result<?> switchAuthType(@PathVariable("appId") Long appId, @RequestBody AppSwitchAuthTypeReq req){
		appService.switchAuthType(RequestContext.currentOrgId(), RequestContext.currentUserId(), appId, req);
		return Result.ok();
	}
	
	@ApiOperation(value = "获取隐藏表单配置",notes = "获取隐藏表单配置")
	@GetMapping("/hidden-config")
	@SneakyThrows
	public Result<?> getUiHiddenConfig(){
	    ClassPathResource resource = new ClassPathResource("hidden-config/"+hiddenConfigFileName+".json");
	    try(InputStream is = resource.getInputStream()){
	        JsonReader reader = new JsonReader(new InputStreamReader(is));
	        Type type = new TypeToken<List<HiddenObjResp>>() {
                private static final long serialVersionUID = 1L;
            }.getType();
            Gson gson = new Gson();
            List<HiddenObjResp> data = gson.fromJson(reader, type);
            return Result.ok(data);
	    }
	}

	@GetMapping("/data-schedule")
	public Result<?> dataSchedule(){
		appService.dataSchedule();
		return Result.ok();
	}
	
}
