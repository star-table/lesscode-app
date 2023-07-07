package com.polaris.lesscode.app.openapi.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

//@Data
//@ApiModel(value="openApi编辑应用请求结构体", description="openApi编辑应用请求结构体")
//public class AppOpenUpdateReq {
//
//	@NotBlank(message = "应用名不能为空")
//	private String name;
//
//	private String icon;
//
//	@ApiModelProperty("应用类型（1表单 2仪表盘）")
//	private Integer type;
//
//	@NotBlank(message = "链接地址不能为空")
//	private String linkUrl;
//
//	@NotBlank(message = "config不能为空")
//	private String config;
//
//	@ApiModelProperty("状态 1：启用 2：禁用")
//	private Integer status;
//
//	private Integer hidden;
//
//}
