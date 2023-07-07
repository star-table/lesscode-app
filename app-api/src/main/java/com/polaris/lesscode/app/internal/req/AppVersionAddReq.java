package com.polaris.lesscode.app.internal.req;

import lombok.Data;

@Data
public class AppVersionAddReq {

	private Integer type;
	
	private String config;
	
	private Integer status;

	private Long userId;
}
