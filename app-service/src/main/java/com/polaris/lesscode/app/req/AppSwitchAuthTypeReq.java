package com.polaris.lesscode.app.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AppSwitchAuthTypeReq {

    @ApiModelProperty("权限类型：1，继承，2，自定义")
    private Integer authType;
}
