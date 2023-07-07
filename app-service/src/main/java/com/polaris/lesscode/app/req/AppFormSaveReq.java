package com.polaris.lesscode.app.req;

import com.polaris.lesscode.form.internal.sula.FieldParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AppFormSaveReq {
    @ApiModelProperty("字段配置")
    private List<FieldParam> config;

    @ApiModelProperty("字段排序")
    private Map<String, Integer> fieldOrders;

    @ApiModelProperty("组织字段")
    private List<String> baseFields;

    @ApiModelProperty("应用名")
    private String name;

    @ApiModelProperty("分组类型")
    private Integer groupType;

    @ApiModelProperty("应用id")
    private Long appId;
}
