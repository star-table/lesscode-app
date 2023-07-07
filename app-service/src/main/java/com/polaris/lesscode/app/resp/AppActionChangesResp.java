package com.polaris.lesscode.app.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用Action变更model
 *
 * @author Nico
 * @date 2021/1/22 10:53
 */
@Data
public class AppActionChangesResp {

    @ApiModelProperty("变动的字段key")
    private String key;

    @ApiModelProperty("变动的字段name")
    private String name;

    @ApiModelProperty("变动前内容")
    private Object before;

    @ApiModelProperty("变动后内容")
    private Object after;

}
