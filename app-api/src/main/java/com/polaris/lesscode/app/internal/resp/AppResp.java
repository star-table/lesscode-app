package com.polaris.lesscode.app.internal.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value="应用信息（内部调用）", description="应用返回信息（内部调用）")
public class AppResp {

	private Long id;

	private Long pkgId;

	private Long orgId;

	private Long extendsId;
	
	private String name;
	
	private Integer type;
	
	private String icon;
	
	private Integer status;
	
	private Long creator;
	
	private Date createTime;
	
	private Long updator;
	
	private Date updateTime;

	private Long parentId;

	@ApiModelProperty("表单id")
	private Long formId;

	@ApiModelProperty("仪表盘id")
	private Long dashboardId;

	private Integer workflowFlag;

	private Integer templateFlag;

	private Long projectId;

	@ApiModelProperty("镜像视图id")
	private Long mirrorViewId;

	@ApiModelProperty("镜像应用id")
	private Long mirrorAppId;

}
