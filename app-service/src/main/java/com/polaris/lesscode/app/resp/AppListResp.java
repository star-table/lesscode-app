package com.polaris.lesscode.app.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="应用列表返回信息", description="应用列表返回信息")
public class AppListResp {

	private List<AppResp> appListResp;

	private List<ProjectResp> projectListResp;

	//private Boolean hasManageAppPackage;

}
