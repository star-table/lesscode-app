package com.polaris.lesscode.app.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value="应用表单流程部署返回信息", description="应用表单流程部署返回信息")
public class AppWorkFlowResp {

	private Long id;
	
	private Long orgId;

	private Long appId;

	private String processKey;

	private Long creator;
	
	private Date createTime;
	
	private Long updator;
	
	private Date updateTime;

}
