package com.polaris.lesscode.app.enums;

public enum YesOrNo {

	YES(1, "是"),

	NO(2, "否");

	private int code;

	private String desc;

	private YesOrNo(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String name) {
		this.desc = desc;
	}

	public static YesOrNo formatOrNull(Integer code) {
		if (code == null) {
			return null;
		}
		YesOrNo[] enums = values();
		for (YesOrNo _enu : enums) {
			if (_enu.getCode().equals(code)) {
				return _enu;
			}
		}
		return null;
	}
	
}
