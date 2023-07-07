package com.polaris.lesscode.app.req;

import lombok.Data;

@Data
public class AppVersionAddReq {

	private Integer type;
	
	private String config;
	
	private Integer status;
}
