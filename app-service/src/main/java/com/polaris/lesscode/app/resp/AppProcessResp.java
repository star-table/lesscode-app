package com.polaris.lesscode.app.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value="应用表单流程实例返回信息", description="应用表单流程实例返回信息")
public class AppProcessResp {

	private Long id;
	
	private Long workflowId;

	private String instanceId;

	private String dataId;

	private Long creator;
	
	private Date createTime;
	
	private Long updator;
	
	private Date updateTime;

}
