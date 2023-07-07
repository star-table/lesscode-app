package com.polaris.lesscode.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("lc_app_process")
public class AppProcess {

	private Long id;

	private Long workflowId;

	private String instanceId;

	private String dataId;

	private Long creator;
	
	private Date createTime;
	
	private Long updator;

	@TableField(update = "now()")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

	@Version
	private Long version;

	private Integer delFlag;

}
