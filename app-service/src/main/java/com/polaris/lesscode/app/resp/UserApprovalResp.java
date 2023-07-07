package com.polaris.lesscode.app.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: Liu.B.J
 * @date: 2020/11/23 15:29
 * @description:
 */
@Data
@ApiModel(value="用户审批相关流程", description="用户审批相关流程")
public class UserApprovalResp {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("当前流程节点")
    private String currentNodeName;

    @ApiModelProperty("申请人")
    private String applyUser;

    @ApiModelProperty("应用表单名称")
    private String appName;

    @ApiModelProperty("流程状态")
    private String procStatus;

    @ApiModelProperty("处理人")
    private String handler;

    @ApiModelProperty("申请时间")
    private Date applyTime;

    @ApiModelProperty("最后更新时间")
    private Date lastUpdateTime;

}
