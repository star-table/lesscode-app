package com.polaris.lesscode.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("lc_app_template_cate")
public class AppTemplateCate {

	private Long id;

	private Long orgId;
	
	private String name;

	private String code;
	
	private Integer type;

	private String bgStyle;

	private String fontStyle;

	private String icon;

	private Long sortId;

	private Long creator;
	
	private Date createTime;
	
	private Long updator;

	private Date updateTime;

	private Integer delFlag;

	private Long version;
}
