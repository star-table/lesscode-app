package com.polaris.lesscode.app.internal.enums;

public enum ActionObjType {

	FORM(1, "表单"),
	DASHBOARD(2, "仪表盘"),
	FOLDER(3, "文件夹"),
	;

	private Integer code;

	private String desc;

	private ActionObjType(int code, String desc) {
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

	public static ActionObjType formatOrNull(Integer code) {
		if (code == null) {
			return null;
		}
		ActionObjType[] enums = values();
		for (ActionObjType _enu : enums) {
			if (_enu.getCode().equals(code)) {
				return _enu;
			}
		}
		return null;
	}
	
}
