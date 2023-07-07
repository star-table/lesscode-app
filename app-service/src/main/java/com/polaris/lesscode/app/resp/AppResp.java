package com.polaris.lesscode.app.resp;

import com.polaris.lesscode.app.entity.Project;
import com.polaris.lesscode.app.enums.YesOrNo;
import com.polaris.lesscode.dashboard.internal.resp.DashboardResp;
import com.polaris.lesscode.form.internal.resp.AppFormResp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@ApiModel(value="应用返回信息", description="应用返回信息")
public class AppResp {
	private Long id;
	
	private Long orgId;

	private Long pkgId;

	private Long sort;

	private String name;

	@ApiModelProperty("表单: 1\n" +
			"仪表盘: 2\n" +
			"文件夹: 3\n" +
			"项目: 4\n" +
			"汇总表: 5\n" +
			"视图镜像: 6\n" +
			"应用包: 7")
	private Integer type;
	
	private String icon;
	
	private Integer status;
	
	private Long creator;
	
	private Date createTime;
	
	private Long updator;
	
	private Date updateTime;

	private Long parentId;

	@ApiModelProperty("项目id")
	private Long projectId;

	@ApiModelProperty("项目类型id")
	private Integer projectTypeId;

	@ApiModelProperty("权限类型：1，继承，2，自定义")
	private Integer authType;

	@ApiModelProperty("应用关联类型")
	private List<Map> appTypes;

	@ApiModelProperty("镜像视图id")
	private Long mirrorViewId;

	@ApiModelProperty("镜像视图的应用id")
	private Long mirrorAppId;

	@ApiModelProperty("镜像类型id")
	private Long mirrorTypeId;

	// 权限
	private Boolean editable = false;

	private Boolean deletable = false;

	// 针对外部链接应用
	private Integer externalApp;

	private String linkUrl;

	private boolean hasExtDelete = false;

	private boolean hasExtUpdate = false;

	private boolean hasExtMove = false;

	private boolean hasStared = false;

	private String remark;

	@ApiModelProperty("工作流flag, 1：是工作流应用，2：否")
	private Integer workflowFlag;

	private Integer hidden = YesOrNo.NO.getCode();
	
	@ApiModelProperty(notes = "仪表盘响应数据")
	private DashboardResp dashboardResp;
	
	@ApiModelProperty(notes = "表单响应数据")
	private AppFormResp appFormResp;
}
