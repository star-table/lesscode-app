package com.polaris.lesscode.app.internal.req;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * 创建应用请求结构体
 *
 * @author Nico
 * @date 2021/3/31 17:02
 */
@Data
public class CreateAppReq {

    @ApiModelProperty("组织ID")
    private Long orgId;

    @ApiModelProperty("应用包ID，可不传")
    private Long pkgId;

    @ApiModelProperty("操作人")
    private Long userId;

    @ApiModelProperty("icon")
    private String icon;

    @ApiModelProperty("应用类型，1：表单，2：仪表盘，3：文件夹，4：项目, 5: 汇总表, 6: 镜像")
    private Integer appType;

    @ApiModelProperty("应用名称")
    private String name;

    @ApiModelProperty("应用配置（表单，仪表盘）")
    private String config;

    @ApiModelProperty("继承的应用ID，非必填")
    private Long extendsId;

    @ApiModelProperty("上级id")
    private Long parentId;

    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("是否隐藏，1隐藏，2不隐藏")
    private Integer hidden;

    @ApiModelProperty("权限认证类型，1：继承，2：自定义")
    private Integer authType;

    @ApiModelProperty("是否是扩展应用")
    private Integer externalApp;

    @ApiModelProperty("链接")
    private String linkUrl;

    @ApiModelProperty("镜像视图id")
    private Long mirrorViewId;

    @ApiModelProperty("镜像应用id")
    private Long mirrorAppId;

    @ApiModelProperty("是否添加全体成员")
    private Boolean addAllMember;

    private Short projectType;
}
