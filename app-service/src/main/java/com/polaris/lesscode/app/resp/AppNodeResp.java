package com.polaris.lesscode.app.resp;

import com.polaris.lesscode.workflow.internal.resp.NodeResp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Liu.B.J
 * @date: 2020/12/7 17:18
 * @description:
 */
@Data
@ApiModel(value="流程节点返回结构体", description="流程节点返回结构体")
public class AppNodeResp {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("类型 0：申请节点，1：审批节点，2：子节点，3：填写节点，4：抄送节点，5：Q-Robot节点")
    private Integer type;

    @ApiModelProperty("应用id")
    private Long appId;

    @ApiModelProperty("orgId")
    private Long orgId;

    @ApiModelProperty("审批类型 1：或签（一名负责人通过或拒绝即可），2：会签（需要所有负责人通过）")
    private Integer approveType;

    @ApiModelProperty(value = "下顺序流节点信息")
    private List<NodeResp> nextNode = new ArrayList<>();

    @ApiModelProperty(value = "前顺序流节点Id")
    private String prevId = "0";

    @ApiModelProperty(value = "后顺序流节点Id")
    private String nextId = "0";

    @ApiModelProperty(value = "负责人")
    private String userInfo;

    @ApiModelProperty("申请人 1：工作区可填，2：指定成员可填，3：所有人可填")
    private Integer applyType;

    @ApiModelProperty(value = "筛选数据")
    private String autoJudges;

    @ApiModelProperty(value = "流程回退 1：之前所有节点，2：上一节点，3：指定节点")
    private String revertConfig;

    @ApiModelProperty(value = "权限设置")
    private String authSettings;

    @ApiModelProperty(value = "留言开关 1：开，2：关")
    private Integer msgSwitch;

    @ApiModelProperty(value = "日志开关 1：开，2：关")
    private Integer logSwitch;

    @ApiModelProperty(value = "处理反馈开关 1：开，2：关")
    private Integer feedbackSwitch;

    @ApiModelProperty(value = "超时")
    private String timeoutConfig;

    @ApiModelProperty(value = "待办转交")
    private String deliverConfig;

    @ApiModelProperty(value = "填写次数")
    private String timesConfig;

    @ApiModelProperty(value = "排序")
    private Integer sort;

}
