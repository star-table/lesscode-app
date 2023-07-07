package com.polaris.lesscode.app.enums;

public enum AppTemplateCategoryCode {

	HOT("HOT", "热门"),
	NEWEST("NEWEST", "最新的"),
	POPULAR("POPULAR", "流行的")
	;

	private String code;

	private String desc;

	private AppTemplateCategoryCode(String code, String desc) {
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

	public static AppTemplateCategoryCode formatOrNull(String code) {
		if (code == null) {
			return null;
		}
		AppTemplateCategoryCode[] enums = values();
		for (AppTemplateCategoryCode _enu : enums) {
			if (_enu.getCode().equals(code)) {
				return _enu;
			}
		}
		return null;
	}
	
}
