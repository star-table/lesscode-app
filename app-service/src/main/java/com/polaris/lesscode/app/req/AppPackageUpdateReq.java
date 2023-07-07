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
 * @date 2020-08-03 1:35 下午
 */
@Data
@ApiModel(value="更新应用包请求结构体", description="更新应用包请求结构体")
public class AppPackageUpdateReq {

    @ApiModelProperty("应用包名称")
    private String name;

    private String remark;

    // 权限
    private Integer scope;

    private List<PermissionMembersItemReq> members;
}
