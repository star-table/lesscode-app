package com.polaris.lesscode.app.enums;

public enum NearMoveType {

	PKG(1, "应用包"),

	APP(2, "应用");

	private int type;

	private String name;

	private NearMoveType(int type, String name) {
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

	public static NearMoveType formatOrNull(Integer type) {
		if (type == null) {
			return null;
		}
		NearMoveType[] enums = values();
		for (NearMoveType _enu : enums) {
			if (_enu.getType().equals(type)) {
				return _enu;
			}
		}
		return null;
	}
	
}
