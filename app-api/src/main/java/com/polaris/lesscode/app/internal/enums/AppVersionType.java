package com.polaris.lesscode.app.internal.enums;

public enum AppVersionType {

	APP_INFO(1, "应用信息"),
	
	FORM(2, "表单"),
	
	WORKFLOW(3, "工作流"),
	
	;
	
	private int type;
	
	private String name;

	private AppVersionType(int type, String name) {
		this.type = type;
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
