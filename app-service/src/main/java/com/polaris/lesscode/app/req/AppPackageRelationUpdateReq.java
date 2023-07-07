package com.polaris.lesscode.app.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: Liu.B.J
 * @Data: 2020/8/31 10:49
 * @Modified:
 */
@Data
@ApiModel(value="修改应用包关联请求结构体", description="修改应用包关联请求结构体")
public class AppPackageRelationUpdateReq {

    @NotNull(message = "应用包id不能为空")
    @ApiModelProperty("应用包id")
    private Long pkgId;

    @NotNull(message = "关联类型不能为空")
    @ApiModelProperty("关联类型(1：星标)")
    private Integer type;

}
