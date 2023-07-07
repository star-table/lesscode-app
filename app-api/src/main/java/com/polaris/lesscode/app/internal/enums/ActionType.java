package com.polaris.lesscode.app.internal.enums;

public enum ActionType {

	CREATE(1, "新建"),
	MODIFY(2, "修改"),
	DELETE(3, "删除"),
	COMMENT(4, "评论"),
	RECYCLE(5, "回收"),
	RECOVER(6, "恢复"),
	DISABLE(7, "禁用"),
	ENABLE(8, "启用"),

	APPROVE(100, "通过"),
	REFUSE(101, "拒绝"),
	REVOKE(102, "撤回"),
	ROLLBACK(103, "回滚"),
	SPONSOR(104, "发起"),
	;

	private Integer code;

	private String desc;

	private ActionType(int code, String desc) {
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

	public static ActionType formatOrNull(Integer code) {
		if (code == null) {
			return null;
		}
		ActionType[] enums = values();
		for (ActionType _enu : enums) {
			if (_enu.getCode().equals(code)) {
				return _enu;
			}
		}
		return null;
	}
	
}
