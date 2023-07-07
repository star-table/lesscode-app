package com.polaris.lesscode.app.internal.enums;

public enum AppRelationType {
	USER(1, "用户"),
	DEPT(2, "部门"),
	ROLE(3, "成员组"),
	STAR(4, "加星"),
	;

	private Integer code;

	private String desc;

	private AppRelationType(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public static AppRelationType formatOrNull(Integer code) {
		if (code == null) {
			return null;
		}
		AppRelationType[] enums = values();
		for (AppRelationType _enu : enums) {
			if (_enu.getCode().equals(code)) {
				return _enu;
			}
		}
		return null;
	}
	
}
