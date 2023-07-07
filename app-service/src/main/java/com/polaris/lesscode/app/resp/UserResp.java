package com.polaris.lesscode.app.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户信息
 *
 * @author Nico
 * @date 2021/1/25 12:00
 */
@Data
public class UserResp {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("头像")
    private String avatar;
}
