package com.polaris.lesscode.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.polaris.lesscode.app.enums.YesOrNo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@TableName("lc_app")
public class App {

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long id;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long pkgId;

	private Long orgId;
	
	private String name;
	
	private Integer type;
	
	private String icon;
	
	private Integer status;

	private Long creator;
	
	private Date createTime;
	
	private Long updator;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long extendsId;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long parentId;

	private Long projectId;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long mirrorViewId;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long mirrorAppId;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long mirrorTypeId;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long mirrorTableId;

	private String remark;

	private Integer workflowFlag;

	private Integer templateFlag;

	private Integer hidden = YesOrNo.NO.getCode();

	@TableField(update = "now()")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

	@Version
	private Long version;

	private Integer externalApp = YesOrNo.NO.getCode();

	private String linkUrl;

	private Long sort;

	private Integer delFlag;

	/**
	 * 权限类型：1，继承，2，自定义
	 **/
	private Integer authType;

	/**
	 * 认证类型切换标识，2 未切换过，1 已切换
	 **/
	private Integer authTypeSwitchFlag;

    /**
     * 是否是 收藏的
     */
    @TableField(exist = false)
    private boolean hasStared;
	
}
