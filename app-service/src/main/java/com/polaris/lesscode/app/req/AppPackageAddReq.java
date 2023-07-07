package com.polaris.lesscode.app.req;

import com.polaris.lesscode.permission.internal.model.req.PermissionMembersItemReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-08-03 11:44 上午
 */
@Data
@ApiModel(value="添加应用包请求结构体", description="添加应用包请求结构体")
public class AppPackageAddReq {
    @NotBlank(message = "应用包名称不能为空")
    @ApiModelProperty("应用包名称")
    private String name;

    @ApiModelProperty("应用包的父ID")
    private Long parentId = 0L;

    // 权限
    private Integer scope;

    private List<PermissionMembersItemReq> members;
}
