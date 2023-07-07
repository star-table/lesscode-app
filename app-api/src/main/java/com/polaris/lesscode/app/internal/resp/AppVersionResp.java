package com.polaris.lesscode.app.internal.resp;

import java.util.Date;

import lombok.Data;

@Data
public class AppVersionResp {

	private Long id;
	
	private Long appId;
	
	private Integer type;
	
	private String config;
	
	private Long version;
	
	private Long creator;
	
	private Date createTime;
	
	private Long updator;
	
	private Date updateTime;
}
