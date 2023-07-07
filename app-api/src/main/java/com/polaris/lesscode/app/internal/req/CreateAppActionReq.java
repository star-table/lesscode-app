package com.polaris.lesscode.app.internal.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 创建应用动作请求结构体
 *
 * @author Nico
 * @date 2021/1/26 20:10
 */
@Data
public class CreateAppActionReq {

    @ApiModelProperty("应用id")
    private Long objId;

    @ApiModelProperty("对象类型，1：表单，2：仪表盘，3：文件夹，4：...'")
    private Integer objType;

    @ApiModelProperty("动作类型, 1: 创建，2: 修改，3：删除，4：评论")
    private Integer action;

    @ApiModelProperty("修改变动")
    private List<CreateAppActionChanges> changes;

    @ApiModelProperty("key的名字")
    private Map<String, String> names;

    @ApiModelProperty("key的类型，暂定string")
    private Map<String, String> types;
}
