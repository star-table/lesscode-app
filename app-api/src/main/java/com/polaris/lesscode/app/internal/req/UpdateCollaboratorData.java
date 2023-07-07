package com.polaris.lesscode.app.internal.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCollaboratorData {
    @ApiModelProperty("数据id")
    private Long DataId;

    @ApiModelProperty("字段成员")
    private List<CollaboratorFieldReq> fieldMembers;
}
