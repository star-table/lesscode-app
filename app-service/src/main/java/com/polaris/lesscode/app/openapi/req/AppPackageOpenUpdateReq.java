package com.polaris.lesscode.app.openapi.req;

import com.polaris.lesscode.permission.internal.model.req.PermissionMembersItemReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

//@Data
//@ApiModel(value="openApi更新应用包请求结构体", description="openApi更新应用包请求结构体")
//public class AppPackageOpenUpdateReq {
//
//    @NotBlank(message = "应用包名称不能为空")
//    @ApiModelProperty("应用包名称")
//    private String name;
//
//    @ApiModelProperty("链接")
//    private String linkUrl;
//
//    // 权限
//    private Integer scope;
//
//    private List<PermissionMembersItemReq> members;
//}
