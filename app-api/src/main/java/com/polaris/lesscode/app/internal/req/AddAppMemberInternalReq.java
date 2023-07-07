package com.polaris.lesscode.app.internal.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddAppMemberInternalReq {

    @ApiModelProperty("应用id")
    private Long appId;

    @ApiModelProperty("关联类型，1：成员，2：部门")
    private Integer relationType;

    @ApiModelProperty("关联id")
    private Long relationId;
}
