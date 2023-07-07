package com.polaris.lesscode.app.req;

import com.polaris.lesscode.form.internal.sula.FieldParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;


@Data
@ApiModel(value="添加应用请求结构体", description="添加应用请求结构体")
public class AppAddReq {

	@NotNull(message = "pkgId不能为空")
	private Long pkgId;

	@NotBlank(message = "应用名不能为空")
	private String name;

	private String icon;

	@NotNull(message = "应用类型不能为空")
	private Integer type;

	@ApiModelProperty("继承的id")
	private Long extendsId;

	@ApiModelProperty("父id")
	private Long parentId;

	private Boolean isExt = false;

	private Integer formType;

	@ApiModelProperty("认证类型，1继承，2自定义")
	private Integer authType;

	@ApiModelProperty("外部应用路径")
	private String linkUrl;

	@ApiModelProperty("工作流flag, 1：是工作流应用，2：否")
	private Integer workflowFlag;

	@ApiModelProperty("表单字段配置")
	private List<FieldParam> formFields;

	@ApiModelProperty("字段排序")
	private Map<String, Integer> fieldOrders;

	@ApiModelProperty("组织字段")
	private List<String> baseFields;

	@ApiModelProperty("镜像视图id")
	private Long mirrorViewId;

	@ApiModelProperty("镜像应用id")
	private Long mirrorAppId;

	@ApiModelProperty("镜像类型id")
	private Long mirrorTypeId;

	//private List<FieldParam> config;

}
