package com.polaris.lesscode.app.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.checker.nullness.compatqual.NullableType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Map;

/**
 * App视图 新增请求模型
 *
 * @author roamer
 * @version v1.0
 * @date 2021/1/22 14:57
 */
@ApiModel("App视图 新增请求模型")
@Data
public class AppViewAddReq implements Serializable {

    private static final long serialVersionUID = -9106711763719308719L;

    /**
     * 视图名称
     */
    @ApiModelProperty(value = "视图名称", required = true)
    @NotBlank(message = "视图名称不可为空")
    private String viewName;

    @ApiModelProperty("视图类型，1：表格视图，2：看板视图，3：照片视图, 4：表单视图")
    private Integer type;

    /**
     * 视图配置
     */
    @ApiModelProperty(value = "视图配置", required = true)
    @NotNull(message = "视图配置不可为空")
    private Map<String, Object> config;

    /**
     * 视图说明
     */
    @ApiModelProperty(value = "视图说明")
    @Size(message = "视图说明最多255个字符", max = 255)
    private String remark;

    /**
     * 是否为私有视图
     */
    @ApiModelProperty(value = "是否为私有视图", required = true)
    @NotNull(message = "请指定是否为私有视图")
    private Boolean isPrivate;

    /**
     * 是否是公共锁定
     */
    @ApiModelProperty(value = "是否为公共锁定视图")
    private Boolean isLocked;
}