package com.polaris.lesscode.app.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AppCollectReq {

    @ApiModelProperty("收藏类型，1：增加收藏，2：取消收藏")
    private Integer collectType;

}
