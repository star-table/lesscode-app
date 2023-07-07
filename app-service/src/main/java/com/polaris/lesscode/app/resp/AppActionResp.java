package com.polaris.lesscode.app.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 应用Action响应结构体
 *
 * @Author Nico
 * @Date 2021/1/21 20:35
 **/
@Data
public class AppActionResp {

    @ApiModelProperty("动作id")
    private Long id;

    @ApiModelProperty("组织id")
    private Long orgId;

    @ApiModelProperty("对象id")
    private Long objId;

    @ApiModelProperty("对象类型，1：表单，2：仪表盘，3：文件夹，4：...'")
    private Integer objType;

    @ApiModelProperty("对象名")
    private String objName;

    @ApiModelProperty("数据id")
    private Long dataId;

    @ApiModelProperty("子表数据id")
    private Long subformDataId;

    @ApiModelProperty("子表key")
    private String subformKey;

    @ApiModelProperty("子表名称")
    private String subformName;

    @ApiModelProperty("是否为子表")
    private boolean isSubform;

    @ApiModelProperty("动作类型, 1: 创建，2: 修改，3：删除，4：评论")
    private Integer action;

    @ApiModelProperty("动作名称")
    private String actionName;

    @ApiModelProperty("变动内容，json格式")
    private List<AppActionChangesResp> changes;

    @ApiModelProperty("是否是评论")
    private Boolean isComment;

    @ApiModelProperty("操作人")
    private UserResp operator;

    @ApiModelProperty("操作时间")
    private Date operateTime;
}
