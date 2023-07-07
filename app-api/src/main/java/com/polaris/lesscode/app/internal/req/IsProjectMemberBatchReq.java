package com.polaris.lesscode.app.internal.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class IsProjectMemberBatchReq {

    @ApiModelProperty("组织id")
    private Long orgId;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("所在的所有部门id")
    private List<Long> refDeptIds;

    @ApiModelProperty("所在的所有用户组id")
    private List<Long> refRoleIds;

    @ApiModelProperty("所有应用id")
    private List<Long> appIds;

    @ApiModelProperty("所有项目id，k为projectId,v为appId")
    private Map<Long, Long> appProjectIds;

}
