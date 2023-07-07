package com.polaris.lesscode.app.vo;

import com.polaris.lesscode.vo.AbstractResultCode;

public enum ResultCode implements AbstractResultCode {
	OK(0,"OK"),
	TOKEN_ERROR(401,"token错误"),
	FORBIDDEN_ACCESS(403,     "无权访问"),
	PATH_NOT_FOUND(404,   "请求地址不存在"),
	PARAM_ERROR(501,"请求参数错误"),
	INTERNAL_SERVER_ERROR(500, "服务器异常"),
	SYS_ERROR_MSG(996,  "系统异常, %s "),
	FAILURE(997,  "业务失败"),
	SYS_ERROR(998,    "系统异常"),
	UNKNOWN_ERROR(999,  "未知错误"),
	SOCKET_TIMEOUT_ERROR(1000,"网络超时请稍后再试"),
	UPDATA_SUCCESS(200,   "更新成功"),
	UPDATA_FAIL(100010,"更新失败"),
	INTERNAL_SERVICE_ERROR(150000,  "内部服务异常"),

	APP_ADD_FAIL(400001, "应用添加失败"),
	APP_MODIFY_FAIL(400002,    "应用更新失败"),
	APP_DELETE_FAIL(400003,    "应用删除失败"),
	APP_NOT_EXIST(400004,  "应用不存在"),
	APP_DISABLE(400005,"应用不可用"),
	APP_VALUE_ADD_FAIL(400006,       "应用数据添加失败"),
	APP_VALUE_NOT_EXIST(400007,        "应用数据不存在"),
	APP_VALUE_MODIFY_FAIL(400008, "应用数据更新失败"),
	APP_VALUE_DELETE_FAIL(400009, "应用数据删除失败"),
	APP_FORM_CONFIG_IS_EMPTY_ERROR(400010,       "应用表单配置为空"),
	APP_PACKAGE_ADD_FAIL(400011,"应用包添加失败"),
	APP_PACKAGE_MODIFY_FAIL(400012,"应用包更新失败"),
	APP_PACKAGE_NOT_EXIST(400013, "应用包不存在"),
	APP_PACKAGE_GROUP_NOT_EXIST(400014,    "应用包组不存在"),
	APP_PACKAGE_GROUP_ADD_FAIL(400015,   "应用包组添加失败"),
	APP_PACKAGE_GROUP_MODIFY_FAIL(400016,      "应用包组更新失败"),
	APP_FORM_NOT_EXIST(400017,       "应用表单不存在"),
	APP_GROUP_ADD_FAIL(400018,       "应用组添加失败"),
	APP_GROUP_MODIFY_FAIL(400019, "应用组更新失败"),
	APP_GROUP_NOT_EXIST(4000020,        "应用组不存在"),
	APP_PACKAGE_RELATION_ADD_FAIL(4000021,      "应用包关联添加失败"),
	APP_PACKAGE_RELATION_DEL_FAIL(4000022,      "应用包关联删除失败"),
	APP_VALUE_ADD_CHECK_FAIL(4000023, "应用数据添加校验失败"),
	APP_FORM_ADD_FAIL(4000024,       "应用表单添加失败"),
	APP_FORM_MODIFY_FAIL(4000025,       "应用表单修改失败"),
	APP_FORM_FIELD_REPEAT(4000026,       "应用表单字段名重复"),
	SAVE_OR_UPDATEAPPPKG_PERMISSION_FAIL(4000027,"创建/修改应用包权限失败"),
	NO_GET_APPPKG_PERMISSION(4000028,"该用户没有获取应用包权限"),
	NO_DEL_APPPKG_PERMISSION(4000029,"该用户删除应用包失败"),
	NO_ADD_APPPKG_PERMISSION(4000030,"该用户没有添加应用包权限"),
	NO_MODIFY_APPPKG_PERMISSION(4000031,"该用户没有修改应用包权限"),
	NO_READ_APP_PERMISSION(4000032,       "该用户没有应用表单查询权限"),
	INIT_FORM_PERMISSION_GROUP_FAIL(4000033,       "初始化应用表单权限组失败"),

	NO_CREATE_APP_PERMISSION(4000034,       "该用户没有应用表单创建权限"),
	NO_MODIFY_APP_PERMISSION(4000035,       "无应用编辑权限"),
	NO_ADD_APP_PERMISSION(4000036,       "没有添加权限"),
	NO_DELETE_APPPKG_PERMISSION(4000037,"没有删除权限"),

	UNSUPPORT_APP_TYPE(4000038, "不支持的应用类型"),
	APP_DEL_PERMISSION_GROUP_FAIL(4000039,       "删除应用权限组失败"),
	APP_PACKAGE_GROUP_DEL_FAIL(4000040,      "该应用包组下包含其他应用包，应用包组删除失败"),
	APP_GROUP_DEL_FAIL(4000041,        "该应用组下包含其他应用，应用组删除失败"),
	APP_PACKAGE_DEL_FAIL(4000042, "该应用包下存在应用组或应用表单，应用包删除失败"),
	APP_PACKAGE_GROUP_NO_MODIFY_PERMISSION(4000043,    "没有应用包组修改权限，应用包组修改失败"),
	APP_PACKAGE_GROUP_NO_ADD_PERMISSION(4000044,    "没有应用包组添加权限，应用包组添加失败"),
	APP_PACKAGE_GROUP_NO_DEL_PERMISSION(4000045,    "没有应用包组删除权限，应用包组删除失败"),
	APP_PACKAGE_GROUP_NO_MOVE_PERMISSION(4000046,    "没有应用包组拖曳权限，应用包组拖曳失败"),
	APP_NO_MOVE_PERMISSION(4000047,    "没有应用移动权限，应用移动失败"),
	APP_GROUP_NO_MOVE_PERMISSION(4000048,    "没有应用移动权限，应用移动失败"),

	APP_PACKAGE_NO_MOVE_PERMISSION(4000049, "没有应用包移动权限，应用包移动失败"),
	MOVE_REQ_ERROR(4000050, "移动请求参数错误"),
	APP_RELATION_ADD_FAIL(4000051, "应用关联添加失败"),
	APP_RELATION_DEL_FAIL(4000052, "应用关联删除失败"),
	APP_FORM_FIELD_UPDATE_FAIL(4000053, "修改表单字段配置权限失败"),
	APP_WORK_FLOW_DEPLOY_FAIL(4000054, "表单工作流部署失败"),
	APP_WORK_FLOW_ADD_FAIL(4000055, "表单工作流信息保存失败"),
	APP_WORK_FLOW_NOT_EXIST(4000056, "表单工作流部署信息不存在"),
	APP_PROCESS_START_FAIL(4000057, "表单流程实例开启失败"),
	WORKBENCH_INSERT_FAIL(4000058, "工作台添加失败"),
	WORKBENCH_NOT_EXIST(4000059, "工作台不存在"),

	ADD_APP_MEMBERS_ERROR_MEMBER_IDS_FORMAT_ERROR(4000060, "添加成员失败，入参格式错误"),
	DEL_APP_MEMBERS_ERROR_MEMBER_IDS_EMPTY_ERROR(4000061, "删除成员失败，要删除的成员为空"),
	APP_AUTH_TYPE_INVALID(4000062, "应用权限类型无效"),
	NO_DELETE_APP_PERMISSION(4000063,       "无应用删除权限"),
	PROJECT_NOT_EXIST(400064,  "项目不存在"),
	NO_APP_MOVE_PERMISSION(4000065,       "仅项目管理员可修改项目路径"),
	APP_NAME_CANNOT_EMPTY(4000066,       "名称不能为空"),
	INIT_APP_CREATOR_PERMISSION_GROUP_FAIL(4000067,       "初始化应用创建人角色失败"),


	// 应用动作
	APP_ACTION_COMMENT_CONTENT_IS_BLANK(4001001, "评论内容不允许为空"),
	APP_ACTION_COMMENT_CONTENT_IS_TOO_LONG(4001002, "评论内容字数控制在5000以内"),
	APP_ACTION_COMMENT_CREATE_FAIL(4001003, "评论创建失败"),
	APP_ACTION_OBJ_INVALID(4001004, "动态操作对象无效"),
	APP_ACTION_INVALID(4001005, "动态操作动作无效"),
	APP_ACTION_CREATE_FAIL(4001006, "动态创建失败"),

	// 应用模板
	APP_TEMPLATE_CONFIG_NOT_INCORRECT_CONFIGURATION(4002001, "应用模板配置不正确，请检查该文件夹下所有应用的配置"),
	APP_TEMPLATE_NOT_EXIST(4002002, "应用模板不存在"),
	APP_TEMPLATE_CONFIG_IS_EMPTY(4002003, "应用模板并未配置任何应用"),
	APP_TEMPLATE_CATE_NON_EXIST(4002004, "应用模板分类不存在"),
	APP_TEMPLATE_CREATE_FAIL(4002005, "应用模板创建失败"),

	// 应用视图
	APP_VIEW_NOT_EXIST(400500, "APP视图不存在或已删除"),
	APP_VIEW_CONF_CONDITION_PARAM_NOT_EMPTY(400501, "APP视图必须具备条件属性"),
	APP_VIEW_LIMIT_ERROR(400502, "视图最多支持创建30个"),

	// 镜像视图
	APP_MIRROR_INVALID_APP_TYPE(400803, "该应用类型不支持建立镜像"),

	;


	private ResultCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	private int code;

	private String message;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static ResultCode parse(int code) {
		for (ResultCode rc : values()) {
			if (rc.getCode() == code) {
				return rc;
			}
		}
		return ResultCode.SYS_ERROR;
	}

	public boolean equals(Integer code) {
		return Integer.valueOf(this.getCode()).equals(code);
	}


}
