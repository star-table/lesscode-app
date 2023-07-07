package com.polaris.lesscode.app.openapi.req;

import com.polaris.lesscode.permission.internal.model.bo.FieldAuthOptionInfoBo;
import com.polaris.lesscode.permission.internal.model.bo.OptAuthOptionInfoBo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


//@Data
//@ApiModel(value="openApi添加应用请求结构体", description="openApi添加应用请求结构体")
//public class AppOpenAddReq {
//
//	private Long appId;
//
//	private Long pkgId = 0L;
//
//	@NotBlank(message = "应用名不能为空")
//	private String name;
//
//	private String icon;
//
//	@NotNull(message = "应用类型不能为空")
//	private Integer type;
//
//	@NotBlank(message = "链接地址不能为空")
//	private String linkUrl;
//
//	@NotBlank(message = "config不能为空")
//	private String config;
//
//	private Long sort;
//
//	// 操作权限选项
//	private List<OptAuthOptionInfoBo> optAuthOptions;
//
//	// 字段权限选项
//	private List<FieldAuthOptionInfoBo> fieldAuthOptions;
//
//	@NotNull(message = "isExt不能是空")
//	private Boolean isExt;
//
//	@NotNull(message = "createTable不能为空")
//	private Boolean createTable;
//
//	private String componentType;
//
//	@NotNull(message = "hidden不能为空")
//	private Integer hidden;
//
//}
