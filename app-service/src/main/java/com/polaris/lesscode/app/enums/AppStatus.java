package com.polaris.lesscode.app.enums;

public enum AppStatus {

	USABLE(1, "可用"),

	DISABLE(2, "禁用")
	;

	private Integer code;

	private String desc;

	private AppStatus(int code, String desc) {
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

	public static AppStatus formatOrNull(Integer code) {
		if (code == null) {
			return null;
		}
		AppStatus[] enums = values();
		for (AppStatus _enu : enums) {
			if (_enu.getCode().equals(code)) {
				return _enu;
			}
		}
		return null;
	}
	
}
