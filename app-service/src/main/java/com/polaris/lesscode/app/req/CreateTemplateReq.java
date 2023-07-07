package com.polaris.lesscode.app.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * 创建模板请求结构体
 *
 * @Author Nico
 * @Date 2021/3/16 16:33
 **/
@Data
public class CreateTemplateReq {

    @ApiModelProperty("模板类型")
    private String type;

    @NotBlank
    @ApiModelProperty("模板名称")
    private String name;

    @NotBlank
    @ApiModelProperty("封面")
    private String cover;

    @ApiModelProperty("可使用范围,1: 模板市场，2：本组织")
    private Integer usableRange;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("要成为模板的应用ID")
    private Long appId;

    @ApiModelProperty("模板中需要哪些资源，内容为key")
    private Set<String> resources;

}
