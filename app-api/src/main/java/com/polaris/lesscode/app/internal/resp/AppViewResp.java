package com.polaris.lesscode.app.internal.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * App视图 数据响应模型（内部调用）
 *
 * @author roamer
 * @version v1.0
 * @date 2021/1/22 14:57
 */
@ApiModel("App视图 数据响应模型（内部调用）")
@Data
public class AppViewResp implements Serializable {

    private static final long serialVersionUID = 2293448564380844905L;
    /**
     * ID
     */
    @ApiModelProperty("ID")
    private Long id;

    /**
     * 组织ID
     */
    @ApiModelProperty("组织ID")
    private Long orgId;

    /**
     * 应用ID
     */
    @ApiModelProperty("应用ID")
    private Long appId;

    /**
     * 视图名称
     */
    @ApiModelProperty(value = "视图名称")
    private String viewName;

    /**
     * 视图配置
     */
    @ApiModelProperty(value = "视图配置")
    private String config;

    /**
     * 视图说明
     */
    @ApiModelProperty(value = "视图说明")
    private String remark;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Long sort;

    /**
     * 是否为私有视图
     */
    @ApiModelProperty("是否为私有视图")
    private Boolean isPrivate;

    /**
     * 视图所有者
     */
    @ApiModelProperty("视图所有者")
    private Long owner;

    @ApiModelProperty("视图类型，1表格，2看板")
    private Integer type;
}