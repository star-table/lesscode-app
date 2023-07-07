package com.polaris.lesscode.app.internal.enums;

public enum AppType {

	FORM(1, "表单"),
	
	DASHBOARD(2, "仪表盘"),

	FOLDER(3, "文件夹"),

	PROJECT(4, "项目"),

	SUMMARY(5, "汇总表"),

	MIRROR(6, "视图镜像"),

	PACKAGE(7, "应用包"),
	;
	
	private Integer code;
	
	private String desc;

	private AppType(int code, String desc) {
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

	public static AppType formatOrNull(Integer code) {
		if (code == null) {
			return null;
		}
		AppType[] enums = values();
		for (AppType _enu : enums) {
			if (_enu.getCode().equals(code)) {
				return _enu;
			}
		}
		return null;
	}

}
