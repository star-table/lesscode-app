package com.polaris.lesscode.app.internal.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * @author: Liu.B.J
 * @date: 2020/11/19 11:53
 * @description: 流程表单信息
 */
@Data
@ApiModel(value="流程表单信息（内部调用）", description="流程表单信息（内部调用）")
public class AppProcessResp {

    private Long id;

    private Long workflowId;

    private String instanceId;

    private String dataId;

    private Long creator;

    private Date createTime;

    private Long updator;

    private Date updateTime;

}
