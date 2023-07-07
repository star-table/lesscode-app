package com.polaris.lesscode.app.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 创建评论请求结构体
 *
 * @Author Nico
 * @Date 2021/1/21 20:35
 **/
@Data
public class CreateCommentReq {

    @ApiModelProperty("应用id")
    private Long objId;

    @ApiModelProperty("对象类型，1：表单，2：仪表盘，3：文件夹，4：子表单")
    private Integer objType;

    @ApiModelProperty("要评论的数据id")
    private Long dataId;

    @ApiModelProperty("评论内容")
    private String content;
}
