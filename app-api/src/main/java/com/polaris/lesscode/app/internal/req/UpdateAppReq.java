package com.polaris.lesscode.app.internal.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 创建应用请求结构体
 *
 * @author Nico
 * @date 2021/3/31 17:02
 */
@Data
public class UpdateAppReq {

    @ApiModelProperty("组织ID")
    private Long orgId;

    @ApiModelProperty("操作人")
    private Long userId;

    @ApiModelProperty("应用id")
    private Long appId;

    @ApiModelProperty("应用名称")
    private String name;
}
