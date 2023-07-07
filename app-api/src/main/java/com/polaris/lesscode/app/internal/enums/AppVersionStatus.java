package com.polaris.lesscode.app.internal.enums;

public enum AppVersionStatus {

	USING(1, "使用中"),
	
	HISTORY(2, "历史版本"),
	
	DRAFT(3, "草稿"),
	
	;
	
	private int status;
	
	private String name;

	private AppVersionStatus(int status, String name) {
		this.status = status;
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
