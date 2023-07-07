package com.polaris.lesscode.app.internal.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCollaboratorReq {
    @ApiModelProperty("组织id")
    private Long OrgId;

    @ApiModelProperty("操作人")
    private Long operatorId;

    @ApiModelProperty("数据信息")
    private List<UpdateCollaboratorData> data;
}
