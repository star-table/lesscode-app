package com.polaris.lesscode.app.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: Liu.B.J
 * @Description: 应用关联返回
 * @Data: 2020/8/31 11:00
 * @Modified:
 */
@Data
@ApiModel(value="应用关联", description="应用关联返回信息")
public class AppRelationResp {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("org_id")
    private Long orgId;

    @ApiModelProperty("应用id")
    private Long appId;

    @ApiModelProperty("用户关联id")
    private Long relationId;

    @ApiModelProperty("关联类型(1：星标)")
    private Integer type;

    @ApiModelProperty("创建人id")
    private Long creator;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人id")
    private Long updator;

    @ApiModelProperty("更新时间")
    private Date updateTime;

}
