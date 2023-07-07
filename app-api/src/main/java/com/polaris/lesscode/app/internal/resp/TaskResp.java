package com.polaris.lesscode.app.internal.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author: Liu.B.J
 * @date: 2020/11/19 17:53
 * @description:
 */
@Data
@ApiModel(value="流程表单任务信息（内部调用）", description="流程表单任务信息（内部调用）")
public class TaskResp {

    private String id;

    private String name;

    private String processInstanceId;

    private Enum DelegationState;

    private String dataId;

}
