package com.polaris.lesscode.app.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AddAppMemberReq {

    @ApiModelProperty("成员id")
    private List<String> memberIds;

    @ApiModelProperty("所分配权限组id")
    private Long perGroupId;
}
