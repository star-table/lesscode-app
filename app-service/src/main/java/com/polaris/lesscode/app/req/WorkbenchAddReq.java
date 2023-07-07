package com.polaris.lesscode.app.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author: Liu.B.J
 * @date: 2020/12/15 11:40
 * @description:
 */
@Data
@ApiModel(value="添加工作台请求结构体", description="添加工作台请求结构体")
public class WorkbenchAddReq {

    @NotBlank(message = "工作台名称不能为空")
    private String name;

    @NotNull(message = "工作台规模必选")
    private Integer sizeType;

    private String description;

    private String face;
}
