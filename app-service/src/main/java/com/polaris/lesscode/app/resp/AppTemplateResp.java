package com.polaris.lesscode.app.resp;

import com.polaris.lesscode.uc.internal.resp.UserInfoResp;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AppTemplateResp {

	@ApiModelProperty("模板主键ID")
	private Long id;

	@ApiModelProperty("组织id")
	private Long orgId;

	@ApiModelProperty("模板名称")
	private String name;

	@ApiModelProperty("模板类型")
	private String type;

	@ApiModelProperty("模板封面")
	private String cover;

	@ApiModelProperty("备注")
	private String remark;

	@ApiModelProperty("解决方案：polaris-work:极星；lesscode：无码平台；polaris-recruit：极星招聘")
	private String solution;

	@ApiModelProperty("可用范围：above-free:免费版以上；free：免费；above-pro：专业版以上'")
	private String usableLevel;

	@ApiModelProperty("热度")
	private Long hot;

	@ApiModelProperty("7天热度")
	private Long hot7;

	@ApiModelProperty("上架时间")
	private Date shelfTime;

	@ApiModelProperty("模板状态：1上架；2未上架")
	private Integer tplStatus;

	@ApiModelProperty("标签")
	private List<AppTemplateCateResp> tags;

	@ApiModelProperty("特征, 1: 热门，2：最新，3：流行")
	private int feature;

	@ApiModelProperty("创建人")
	private UserInfoResp author;

	@ApiModelProperty("分类id")
	private Long categoryId;

	@ApiModelProperty("是否已上传")
	private Integer isUploaded;

	@ApiModelProperty("APP ID")
	private Long appId;
}
