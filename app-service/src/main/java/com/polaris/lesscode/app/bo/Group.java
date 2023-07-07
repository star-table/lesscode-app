package com.polaris.lesscode.app.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Liu.B.J
 */
@Data
public class Group {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("前一个id")
    private Long beforeId;

    @ApiModelProperty("后一个id")
    private Long afterId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("参照物类型(1：应用包 2：应用)")
    private Integer type;

}
