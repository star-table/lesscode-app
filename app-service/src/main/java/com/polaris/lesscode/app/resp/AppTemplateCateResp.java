package com.polaris.lesscode.app.resp;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AppTemplateCateResp {

	@ApiModelProperty("分类id")
	private Long id;

	@ApiModelProperty("模板名称")
	private String name;

	@ApiModelProperty("模板code")
	private String code;

	@ApiModelProperty("分类类型")
	private Integer type;

	@ApiModelProperty("背景色")
	private String bgStyle;

	@ApiModelProperty("字体色")
	private String fontStyle;

	@ApiModelProperty("icon")
	private String icon;

	@ApiModelProperty("排序值")
	private Long sortId;

}
