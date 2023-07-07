package com.polaris.lesscode.app.internal.api;

import com.polaris.lesscode.context.RequestContext;
import com.polaris.lesscode.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(tags="应用加星")
@RequestMapping("/app/inner/api/v1/apps/{appId}/stars")
public interface AppStarApi {

    @ApiOperation("收藏应用")
    @PostMapping
    Result<Boolean> addStar(@PathVariable("appId") Long appId, @RequestParam("orgId") Long orgId, @RequestParam("userId") Long userId);

    @ApiOperation("取消应用收藏")
    @DeleteMapping
    Result<Boolean> deleteStar(@PathVariable("appId") Long appId, @RequestParam("orgId") Long orgId, @RequestParam("userId") Long userId);

}
