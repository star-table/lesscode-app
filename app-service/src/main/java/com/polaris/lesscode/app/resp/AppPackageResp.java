package com.polaris.lesscode.app.resp;

import com.polaris.lesscode.permission.internal.model.bo.PermissionMembersBo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-08-03 11:41 上午
 */
@Data
@ApiModel(value="应用包", description="应用包返回信息")
public class AppPackageResp {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("组织ID")
    private Long orgId;

    @ApiModelProperty("应用包父级id")
    private Long parentId;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("应用包名称")
    private String name;

    @ApiModelProperty("应用包关联类型")
    private List<Map> pkgTypes;

    @ApiModelProperty("应用包icon")
    private String icon;

    @ApiModelProperty("创建人id")
    private Long creator;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人id")
    private Long updator;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    // 权限
    private Integer scope;

    private PermissionMembersBo members;

    //private List<AppPkgPerItem> appPkgPerList;

    @ApiModelProperty("管理应用包")
    private Boolean managePkg;

    @ApiModelProperty("可编辑")
    private Boolean editable;

    @ApiModelProperty("可删除")
    private Boolean deletable;

    @ApiModelProperty("是否为外部应用包（1是 2否）")
    private Integer externalPkg;

    @ApiModelProperty("外部应用包链接")
    private String linkUrl;

    @ApiModelProperty("外部应用包能否删除")
    private boolean hasExtDelete = false;

    @ApiModelProperty("外部应用包能否修改")
    private boolean hasExtUpdate = false;

    @ApiModelProperty("外部应用包能否移动")
    private boolean hasExtMove = false;

    @ApiModelProperty("备注")
    private String remark;

}
