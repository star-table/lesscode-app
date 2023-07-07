package com.polaris.lesscode.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("lc_app_template_apply_log")
public class AppTemplateApplyLog {

	private Long id;

	private Long orgId;

	private Long tplId;
	
	private Long creator;
	
	private Date createTime;
	
	private Long updator;

	private Date updateTime;

	private Integer delFlag;

	private Long version;

}
