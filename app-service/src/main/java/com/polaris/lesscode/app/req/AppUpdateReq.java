package com.polaris.lesscode.app.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value="编辑应用请求结构体", description="编辑应用请求结构体")
public class AppUpdateReq {

	private String name;
	
	private String icon;

	@ApiModelProperty("应用类型（1表单 2仪表盘）")
	private Integer type;

	private String remark;

	@ApiModelProperty("继承的应用")
	private Long extendsId;

	@ApiModelProperty("外部应用路径")
	private String linkUrl;

	@ApiModelProperty("工作流flag, 1：是工作流应用，2：否")
	private Integer workflowFlag;

}
