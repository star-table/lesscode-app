package com.polaris.lesscode.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("lc_app_relation")
public class AppRelation {

	private Long id;
	
	private Long appId;

	private Long orgId;
	
	private Integer type;
	
	private Long relationId;

	private Long creator;
	
	private Date createTime;
	
	private Long updator;

	@TableField(update = "now()")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

	@Version
	private Long version;
	
	private Integer delFlag;

	public AppRelation(Long appId, Long orgId, Integer type, Long relationId, Long creator, Long updator) {
		this.appId = appId;
		this.orgId = orgId;
		this.type = type;
		this.relationId = relationId;
		this.creator = creator;
		this.updator = updator;
	}

	public AppRelation() {
	}
}
