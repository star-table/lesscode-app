package com.polaris.lesscode.app.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

/**
 * App视图 新增请求模型
 *
 * @author roamer
 * @version v1.0
 * @date 2021/1/22 14:57
 */
@ApiModel("App视图 修改请求模型")
@Data
public class AppViewEditReq implements Serializable {

    private static final long serialVersionUID = -9106711763719308719L;

    /**
     * 视图名称
     */
    @ApiModelProperty(value = "视图名称", required = true)
    private String viewName;

    @ApiModelProperty("视图类型，1：表格视图，2：看板视图，3：照片视图, 4：表单视图")
    private Integer type;

    /**
     * 视图配置
     */
    @ApiModelProperty(value = "视图配置", required = true)
    private Map<String, Object> config;

    /**
     * 视图说明
     */
    @ApiModelProperty(value = "视图说明")
    private String remark;

    @ApiModelProperty
    private Boolean isPrivate;

    @ApiModelProperty(value = "是否为公共锁定视图")
    private Boolean isLocked;

}