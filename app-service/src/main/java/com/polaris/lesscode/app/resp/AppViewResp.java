package com.polaris.lesscode.app.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * App视图 数据响应模型
 *
 * @author roamer
 * @version v1.0
 * @date 2021/1/22 14:57
 */
@ApiModel("App视图 数据响应模型")
@Data
public class AppViewResp implements Serializable {

    private static final long serialVersionUID = -9106711763719308719L;

    /**
     * ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty("应用ID")
    private Long appId;

    /**
     * 视图类型
     */
    @ApiModelProperty("视图类型")
    private Integer type;

    /**
     * 视图名称
     */
    @ApiModelProperty(value = "视图名称")
    private String viewName;

    /**
     * 视图配置
     */
    @ApiModelProperty(value = "视图配置")
    private Map<String, Object> config;

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
     * 是否是公共锁定
     */
    @ApiModelProperty("是否是公共锁定")
    private Boolean isLocked;

    /**
     * 视图所有者
     */
    @ApiModelProperty("视图所有者")
    private Long owner;

    public AppViewResp() {
        config = Collections.emptyMap();
    }
}