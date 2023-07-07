package com.polaris.lesscode.app.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
@TableName("lc_app_version")
public class AppVersion {

	private Long id;
	
	private Long appId;
	
	@TableField("`type`")
	private Integer type;
	
	private Integer status;
	
	private String config;

	@Version
	private Long version;
	
	private Long creator;
	
	private Date createTime;
	
	private Long updator;

	@TableField(update = "now()")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;
	
	private Integer delFlag;
}
