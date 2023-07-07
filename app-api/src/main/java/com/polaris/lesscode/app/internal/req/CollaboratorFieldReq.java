package com.polaris.lesscode.app.internal.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CollaboratorFieldReq {
    @ApiModelProperty("字段id")
    private String FieldName;

    @ApiModelProperty("增加的成员")
    private List<String> addMembers;

    @ApiModelProperty("减少的成员")
    private List<String> delMembers;
}
