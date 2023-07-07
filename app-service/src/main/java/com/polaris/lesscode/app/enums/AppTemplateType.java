package com.polaris.lesscode.app.enums;

public enum AppTemplateType {

	PUBLIC("PUBLIC", "公共"),
	PRIVATE("PRIVATE", "私有"),

	;

	private String code;

	private String desc;

	AppTemplateType(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public static AppTemplateType formatOrNull(String code) {
		if (code != null) {
			AppTemplateType[] enums = values();
			for (AppTemplateType _enu : enums) {
				if (_enu.getCode().equals(code)) {
					return _enu;
				}
			}
		}
		return null;
	}
	
}
