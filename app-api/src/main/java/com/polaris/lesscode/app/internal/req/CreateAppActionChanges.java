package com.polaris.lesscode.app.internal.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 动作变动
 *
 * @author Nico
 * @date 2021/3/2 15:06
 */
@Data
public class CreateAppActionChanges {

    @ApiModelProperty("数据id")
    private Long dataId;

    @ApiModelProperty("子数据id")
    private Long subformDataId;

    @ApiModelProperty("子表单字段key")
    private String subformKey;

    @ApiModelProperty("子表单字段名")
    private String subformName;

    @ApiModelProperty("修改前")
    private Map<String, Object> before;

    @ApiModelProperty("修改后")
    private Map<String, Object> after;
}
