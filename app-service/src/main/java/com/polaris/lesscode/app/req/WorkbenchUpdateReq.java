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
@ApiModel(value="更新工作台请求结构体", description="更新工作台请求结构体")
public class WorkbenchUpdateReq {

    @NotNull(message = "id不能为空")
    private Long id;

    private String name;

    private Integer sizeType;

    private String description;

    private String face;
}
