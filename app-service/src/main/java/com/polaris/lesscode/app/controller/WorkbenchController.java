package com.polaris.lesscode.app.controller;

import com.polaris.lesscode.app.req.WorkbenchAddReq;
import com.polaris.lesscode.app.req.WorkbenchUpdateReq;
import com.polaris.lesscode.app.resp.WorkbenchResp;
import com.polaris.lesscode.app.service.WorkbenchService;
import com.polaris.lesscode.context.RequestContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.polaris.lesscode.vo.Result;

import java.util.List;

/**
 * @author: Liu.B.J
 * @date: 2020/12/15 11:35
 * @description:
 */
@Api(tags="工作台管理")
@RestController
@RequestMapping("/app/api/v1/workbenchs")
public class WorkbenchController {

    @Autowired
    private WorkbenchService workbenchService;

    @ApiOperation(value="获取工作台信息", notes="获取工作台信息")
    @GetMapping
    public Result<List<WorkbenchResp>> getWorkbenchList(){
        return Result.ok(workbenchService.getWorkbench(RequestContext.currentUserId(), RequestContext.currentOrgId()));
    }

    @ApiOperation(value="添加工作台", notes="添加工作台")
    @PostMapping
    public Result<WorkbenchResp> addWorkbench(@Validated @RequestBody WorkbenchAddReq req){
        return Result.ok(workbenchService.addWorkbench(RequestContext.currentUserId(), RequestContext.currentOrgId(), req));
    }

    @ApiOperation(value="删除工作台", notes="删除工作台")
    @DeleteMapping
    public Result<Boolean> delWorkbench(@RequestParam("id") Long id){
        return Result.ok(workbenchService.delWorkbench(RequestContext.currentUserId(), id));
    }

    @ApiOperation(value="修改工作台", notes="修改工作台")
    @PutMapping
    public Result<WorkbenchResp> updateWorkbench(@Validated @RequestBody WorkbenchUpdateReq req){
        return Result.ok(workbenchService.updateWorkbench(RequestContext.currentUserId(), req));
    }

}
