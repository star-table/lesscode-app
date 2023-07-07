package com.polaris.lesscode.app.enums;

public enum ReferenceType {

	BEFORE(1, "before"),

	AFTER(2, "after");

	private int type;

	private String name;

	private ReferenceType(int type, String name) {
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
