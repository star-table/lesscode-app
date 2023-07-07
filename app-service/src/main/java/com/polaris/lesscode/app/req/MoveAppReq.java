package com.polaris.lesscode.app.req;

import com.polaris.lesscode.app.bo.Group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Liu.B.J
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="移动应用分组更新", description="移动应用分组更新")
public class MoveAppReq extends Group {

    @ApiModelProperty("应用的上级id")
    private Long parentId;

}
