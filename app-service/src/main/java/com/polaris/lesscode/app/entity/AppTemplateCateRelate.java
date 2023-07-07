package com.polaris.lesscode.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("lc_app_template_cate_relate")
public class AppTemplateCateRelate {

	private Long id;

	private Long orgId;

	private Long tplId;

	private Integer relateType;

	private Long relateId;

	private Long creator;
	
	private Date createTime;
	
	private Long updator;

	private Date updateTime;

	private Integer delFlag;

	private Long version;

}
