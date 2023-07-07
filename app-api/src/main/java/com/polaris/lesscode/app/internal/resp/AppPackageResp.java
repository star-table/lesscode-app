package com.polaris.lesscode.app.internal.resp;

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
@ApiModel(value="应用包(内部调用)", description="应用包返回信息(内部调用)")
public class AppPackageResp {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("应用包上级")
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

}
