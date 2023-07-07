package com.polaris.lesscode.app.internal.api;

import com.polaris.lesscode.app.internal.resp.AppPackageResp;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: Liu.B.J
 * @data: 2020/9/19 10:17
 * @modified:
 */
@Api(value="应用包管理（内部调用）", tags={"应用包管理（内部调用）"})
@RequestMapping("/app/inner/api/v1/app/packages")
public interface AppPackageApi {

    @ApiOperation(value="获取应用包列表", notes="获取应用包列表")
    @GetMapping("/get-apppkg-list")
    public Result<List<AppPackageResp>> getAppPkgList(
            @RequestParam(value = "orgId") Long orgId);

    @ApiOperation(value="获取应用包信息", notes="获取应用包信息")
	@GetMapping("/get-apppkg-info")
	public Result<AppPackageResp> getAppPkgInfo(
	        @RequestParam("pkgId") Long pkgId);

}
