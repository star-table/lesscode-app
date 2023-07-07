package com.polaris.lesscode.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("lc_app_template")
public class AppTemplate {

	private Long id;

	private Long orgId;
	
	private String name;
	
	private String type;
	
	private String cover;

	private String remark;

	private String config;

	private String solution;

	private String usableLevel;

	private Long hot;

	private Date shelfTime;

	private Integer tplStatus;
	
	private Integer status;
	
	private Long creator;
	
	private Date createTime;
	
	private Long updator;

	private Date updateTime;

	private Integer delFlag;

	private Long version;

	private Integer isShow;

	private Long uploadOrgId;

	private String uploader;

	private Integer isUploaded;
}
